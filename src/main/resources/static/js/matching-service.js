/**
 * matching-service.js
 * 매칭 핵심 로직 (hide_front/src/services/matchingService.js 이관)
 */
window.MatchingService = (function() {
  const STORAGE_KEYS = {
    LIKES: 'hide_likes',
    MATCHES: 'hide_matches',
    DIRECT_COUNT: 'hide_direct_count',
    NOTIFICATIONS: 'hide_notifications',
  };

  const LIKE_TYPE = { LIKE: 'LIKE', DISLIKE: 'DISLIKE', DIRECT: 'DIRECT' };
  const MAX_DAILY_DIRECTS = 3;

  function getFromStorage(key, fallback) {
    if (fallback === undefined) fallback = [];
    try { const raw = localStorage.getItem(key); return raw ? JSON.parse(raw) : fallback; }
    catch(e) { return fallback; }
  }
  function saveToStorage(key, value) { localStorage.setItem(key, JSON.stringify(value)); }
  function getTodayStr() { return new Date().toISOString().slice(0, 10); }

  function faceMatchScore(preference, features) {
    if (!preference || !features) return 0;
    const len = Math.min(preference.length, features.length);
    if (len === 0) return 0;
    let match = 0;
    for (let i = 0; i < len; i++) { if (preference[i] === features[i]) match++; }
    return match / len;
  }

  function hobbyOverlap(myInterests, theirInterests) {
    if (!myInterests || !theirInterests) return 0;
    const mySet = new Set(myInterests.map(t => t.replace('#', '').trim()));
    return theirInterests.filter(t => mySet.has(t.replace('#', '').trim())).length;
  }

  function calculateCompatibility(myProfile, otherProfile) {
    const faceScore = faceMatchScore(myProfile.facePreference, otherProfile.faceFeatures) * 100;
    const absScore = otherProfile.absoluteScore || 50;
    const overlap = hobbyOverlap(myProfile.interests, otherProfile.interests);
    const hobbyScore = Math.min(overlap / 3, 1) * 100;
    return Math.round(faceScore * 0.4 + absScore * 0.3 + hobbyScore * 0.3);
  }

  function getRecommendedProfiles(myProfile, allProfiles) {
    const likes = getFromStorage(STORAGE_KEYS.LIKES);
    const actedOnIds = new Set(likes.filter(l => l.fromUserId === myProfile.id).map(l => l.toUserId));
    return allProfiles
      .filter(p => p.id !== myProfile.id && !actedOnIds.has(p.id))
      .map(p => ({ ...p, compatibility: calculateCompatibility(myProfile, p) }))
      .sort((a, b) => b.compatibility - a.compatibility);
  }

  function incrementDirectCount(userId) {
    const today = getTodayStr();
    const key = STORAGE_KEYS.DIRECT_COUNT + '_' + userId;
    const data = getFromStorage(key, { date: today, count: 0 });
    if (data.date !== today) { saveToStorage(key, { date: today, count: 1 }); }
    else { data.count++; saveToStorage(key, data); }
  }

  function getRemainingDirects(userId) {
    const today = getTodayStr();
    const key = STORAGE_KEYS.DIRECT_COUNT + '_' + userId;
    const data = getFromStorage(key, { date: today, count: 0 });
    if (data.date !== today) return MAX_DAILY_DIRECTS;
    return Math.max(0, MAX_DAILY_DIRECTS - data.count);
  }

  function addNotification(fromUserId, toUserId, matchId) {
    const notifications = getFromStorage(STORAGE_KEYS.NOTIFICATIONS);
    notifications.push({ id: 'noti_' + Date.now() + '_1', type: 'MATCH', matchId, userId: fromUserId, partnerId: toUserId, message: '새로운 매칭이 성사되었어요! 🎉', createdAt: new Date().toISOString(), read: false });
    notifications.push({ id: 'noti_' + Date.now() + '_2', type: 'MATCH', matchId, userId: toUserId, partnerId: fromUserId, message: '새로운 매칭이 성사되었어요! 🎉', createdAt: new Date().toISOString(), read: false });
    saveToStorage(STORAGE_KEYS.NOTIFICATIONS, notifications);
  }

  function checkAndCreateMatch(fromUserId, toUserId) {
    const likes = getFromStorage(STORAGE_KEYS.LIKES);
    const mutualLike = likes.find(l => l.fromUserId === toUserId && l.toUserId === fromUserId && [LIKE_TYPE.LIKE, LIKE_TYPE.DIRECT].includes(l.type));
    if (!mutualLike) return null;
    const matches = getFromStorage(STORAGE_KEYS.MATCHES);
    const existing = matches.find(m => (m.user1Id === fromUserId && m.user2Id === toUserId) || (m.user1Id === toUserId && m.user2Id === fromUserId));
    if (existing) return null;
    const newMatch = { id: 'match_' + Date.now(), user1Id: fromUserId, user2Id: toUserId, matchedAt: new Date().toISOString(), active: true };
    matches.push(newMatch);
    saveToStorage(STORAGE_KEYS.MATCHES, matches);
    addNotification(fromUserId, toUserId, newMatch.id);
    return newMatch;
  }

  function sendLike(fromUserId, toUserId, type) {
    if (type === LIKE_TYPE.DIRECT) {
      const remaining = getRemainingDirects(fromUserId);
      if (remaining <= 0) return { success: false, matched: false, error: '오늘 다이렉트 메시지를 모두 사용했어요. (3/3)' };
      incrementDirectCount(fromUserId);
    }
    const likes = getFromStorage(STORAGE_KEYS.LIKES);
    const existing = likes.find(l => l.fromUserId === fromUserId && l.toUserId === toUserId);
    if (existing) return { success: false, matched: false, error: '이미 반응한 프로필이에요.' };
    likes.push({ id: 'like_' + Date.now() + '_' + Math.random().toString(36).slice(2, 6), fromUserId, toUserId, type, createdAt: new Date().toISOString() });
    saveToStorage(STORAGE_KEYS.LIKES, likes);
    if (type === LIKE_TYPE.LIKE|| type === LIKE_TYPE.DIRECT) {
      const matched = checkAndCreateMatch(fromUserId, toUserId);
      if (matched) return { success: true, matched: true, matchData: matched };
    }
    return { success: true, matched: false };
  }

  function getNotifications(userId) {
    return getFromStorage(STORAGE_KEYS.NOTIFICATIONS).filter(n => n.userId === userId).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }
  function getUnreadNotificationCount(userId) { return getNotifications(userId).filter(n => !n.read).length; }
  function markAllNotificationsRead(userId) {
    const noti = getFromStorage(STORAGE_KEYS.NOTIFICATIONS);
    noti.forEach(n => { if (n.userId === userId) n.read = true; });
    saveToStorage(STORAGE_KEYS.NOTIFICATIONS, noti);
  }

  return {
    LIKE_TYPE, calculateCompatibility, getRecommendedProfiles,
    sendLike, getRemainingDirects, getNotifications,
    getUnreadNotificationCount, markAllNotificationsRead,
  };
})();
