package com.alura.service;

import static java.awt.Transparency.TRANSLUCENT;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.alura.exception.StickerApiException;
import com.alura.model.ParamSticker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Sticker {

  public void create(ParamSticker param) throws StickerApiException {
    try {
      BufferedImage original = ImageIO.read(param.getImage());
      var size = new StickerSize(original.getWidth(), original.getHeight());

      formartSize(param, original, size);

      BufferedImage sticker = new BufferedImage(size.getWidth(), size.getHeight() + param.getHeightPlus(),
          TRANSLUCENT);

      Graphics2D graphic = (Graphics2D) sticker.getGraphics();
      graphic.drawImage(original, 0, 0, size.getWidth(), size.getHeight(), null);

      formatTopImage(param, sticker, graphic);

      formatText(param, size, sticker, graphic);

      Files.createDirectories(Paths.get(param.getOutputPath()));
      ImageIO.write(sticker, param.getOutputFormat(),
          new File(param.getOutputPath() + param.getOutputName() + "." + param.getOutputFormat()));
    } catch (IOException e) {
      throw new StickerApiException("Erro ao criar Sticker.", e);
    }
  }

  private void formartSize(ParamSticker param, BufferedImage original, StickerSize size) {
    if (size.getWidth() > param.getTargetWidth() || size.getHeight() > param.getTargetHeight()) {
      Double scale = Math.min(
          size.getWidth() > param.getTargetWidth() ? param.getTargetWidth() / Double.valueOf(size.getWidth()) : 1,
          size.getHeight() > param.getTargetHeight() ? param.getTargetHeight() / Double.valueOf(size.getHeight())
              : 1);
      size.setWidth((int) (original.getWidth() * scale));
      size.setHeight((int) (original.getHeight() * scale));
    }
  }

  private void formatTopImage(ParamSticker param, BufferedImage sticker, Graphics2D graphic) throws IOException {
    if (param.isTop()) {
      BufferedImage topImage = ImageIO.read(new File(param.getTopImage()));
      graphic.drawImage(
          topImage, sticker.getWidth() - topImage.getWidth() + 35,
          sticker.getHeight() - topImage.getHeight(),
          null);
    }
  }

  private void formatText(ParamSticker param, StickerSize size, BufferedImage sticker, Graphics2D graphic) {
    var font = new Font(param.getFontName(), param.getFontStyle(), param.getFontSize());

    Shape textShape = new TextLayout(
        param.getText(), font, graphic.getFontRenderContext())
        .getOutline(scaleText(param.getText(), size.getWidth(), font));

    graphic.translate((sticker.getWidth() - textShape.getBounds().width) / 2,
        ((param.getHeightPlus() - textShape.getBounds().height) / 2) + textShape.getBounds().height
            + size.getHeight());

    graphic.setColor(param.getTextColor());
    graphic.fill(textShape);
    graphic.setStroke(new BasicStroke(param.getStrokeNumber()));
    graphic.setColor(param.getStrokeColor());
    graphic.draw(textShape);
  }

  private AffineTransform scaleText(String text, int width, Font font) {
    var sizeFont = font.getStringBounds(text, new FontRenderContext(null, true, true));
    if (sizeFont.getWidth() >= width) {
      AffineTransform textScaled = new AffineTransform();
      var scale = (Double.valueOf(width) / sizeFont.getWidth()) - .1;
      textScaled.scale(scale, scale);
      return textScaled;
    }
    return null;
  }
}

@Getter
@Setter
@AllArgsConstructor
class StickerSize {
  private int width;
  private int height;
}
