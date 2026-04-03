/**
 * app.js – 공통 유틸리티
 */
window.HideApp = (function() {
  const ONBOARDING_KEY = 'hide_onboarding_done';
  const SETUP_KEY = 'hide_setup_done';

  function isOnboardingDone() { return localStorage.getItem(ONBOARDING_KEY) === 'true'; }
  function isSetupDone() { return localStorage.getItem(SETUP_KEY) === 'true'; }
  function completeOnboarding() { localStorage.setItem(ONBOARDING_KEY, 'true'); }
  function completeSetup() { localStorage.setItem(SETUP_KEY, 'true'); }
  function resetAll() { localStorage.removeItem(ONBOARDING_KEY); localStorage.removeItem(SETUP_KEY); }

  function goBack() { window.history.back(); }
  function navigate(path) { window.location.href = path; }

  // 하단 네비게이션 활성 탭 표시
  function initBottomNav() {
    const path = window.location.pathname;
    document.querySelectorAll('.bottom-nav a').forEach(function(a) {
      const href = a.getAttribute('href');
      const isActive = href === '/' ? path === '/' : path.startsWith(href);
      if (isActive) a.classList.add('active');
      else a.classList.remove('active');
    });
  }

  // 하단 네비게이션 숨김 경로 체크
  function shouldHideNav() {
    const path = window.location.pathname;
    const hidePaths = ['/messages/', '/profile/', '/likes', '/profile/edit', '/tier-missions', '/support'];
    return hidePaths.some(function(p) { return path.startsWith(p) || path === p; });
  }

  document.addEventListener('DOMContentLoaded', function() {
    initBottomNav();
    if (shouldHideNav()) {
      var nav = document.querySelector('.bottom-nav');
      if (nav) nav.style.display = 'none';
    }
  });

  return { isOnboardingDone, isSetupDone, completeOnboarding, completeSetup, resetAll, goBack, navigate };
})();
