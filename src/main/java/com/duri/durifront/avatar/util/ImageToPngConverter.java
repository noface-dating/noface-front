package com.duri.durifront.avatar.util;

import com.duri.durifront.avatar.exception.AvatarException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 * OpenAI {@code POST /v1/images/edits} (dall-e-2) 요구사항:
 * <ul>
 *   <li>PNG, 픽셀 모드 RGBA/LA/L (단순 RGB PNG는 거절)</li>
 *   <li><strong>정사각형</strong> 이미지 — 세로/가로 사진은 중앙 기준 정사각 크롭 후 스케일</li>
 * </ul>
 */
public final class ImageToPngConverter {

    /** DALL-E 2 편집 API가 기대하는 정사각 입력(출력 size 와 맞춤) */
    private static final int OPENAI_EDIT_EDGE_PX = 1024;

    private ImageToPngConverter() {}

    public static byte[] convertToOpenAiPng(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new AvatarException("이미지 데이터가 비어 있습니다.");
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(imageBytes)) {
            BufferedImage src = ImageIO.read(in);
            if (src == null) {
                throw new AvatarException(
                        "이미지를 읽을 수 없습니다. JPG 또는 PNG 사진을 사용해 주세요.");
            }
            int w = src.getWidth();
            int h = src.getHeight();
            if (w < 8 || h < 8) {
                throw new AvatarException("이미지가 너무 작습니다. 더 큰 사진을 올려 주세요.");
            }
            // 1) 정사각 중앙 크롭 (API 필수)
            int side = Math.min(w, h);
            int x0 = (w - side) / 2;
            int y0 = (h - side) / 2;
            BufferedImage square = src.getSubimage(x0, y0, side, side);

            // 2) 1024×1024 RGBA + 흰 배경 (스케일 품질)
            BufferedImage rgba = new BufferedImage(OPENAI_EDIT_EDGE_PX, OPENAI_EDIT_EDGE_PX, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = rgba.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, OPENAI_EDIT_EDGE_PX, OPENAI_EDIT_EDGE_PX);
                g.setComposite(AlphaComposite.SrcOver);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(square, 0, 0, OPENAI_EDIT_EDGE_PX, OPENAI_EDIT_EDGE_PX, null);
            } finally {
                g.dispose();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (!ImageIO.write(rgba, "png", out)) {
                throw new AvatarException("PNG 변환에 실패했습니다.");
            }
            return out.toByteArray();
        } catch (AvatarException e) {
            throw e;
        } catch (Exception e) {
            throw new AvatarException("이미지 변환 중 오류가 발생했습니다.", e);
        }
    }
}
