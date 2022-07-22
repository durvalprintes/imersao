package com.alura.service;

import static java.awt.Transparency.TRANSLUCENT;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import javax.imageio.ImageIO;

import com.alura.model.Sticker;

@SuppressWarnings("squid:S106")
public interface WebService {

  default String jsonFromGet(String url) throws IOException, InterruptedException {
    return HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
            BodyHandlers.ofString())
        .body();
  }

  default void printField(String format, String field) {
    System.out.println(format + field + "\033[0m");
  }

  public void print();

  public void generateStickerImage(Sticker param);

  default void createSticker(Sticker param) throws IOException {

    BufferedImage original = ImageIO.read(param.getImage());
    int width = original.getWidth();
    int height = original.getHeight();

    if (width > param.getTargetWidth() || height > param.getTargetHeight()) {
      Double scale = Math.min(
          width > param.getTargetWidth() ? param.getTargetWidth() / Double.valueOf(width) : 1,
          height > param.getTargetHeight() ? param.getTargetHeight() / Double.valueOf(height) : 1);
      width = (int) (original.getWidth() * scale);
      height = (int) (original.getHeight() * scale);
    }

    BufferedImage sticker = new BufferedImage(width, height + param.getHeightPlus(),
        TRANSLUCENT);

    Graphics2D graphic = (Graphics2D) sticker.getGraphics();
    graphic.drawImage(original, 0, 0, width, height, null);

    if (param.isTop()) {
      BufferedImage topImage = ImageIO.read(new File(param.getTopImage()));
      graphic.drawImage(
          topImage, sticker.getWidth() - topImage.getWidth() + 35,
          sticker.getHeight() - topImage.getHeight(),
          null);
    }

    var font = new Font(param.getFontName(), param.getFontStyle(), param.getFontSize());
    Shape textShape = new TextLayout(
        param.getText(), font, graphic.getFontRenderContext()).getOutline(null);

    graphic.translate((sticker.getWidth() - textShape.getBounds().width) / 2,
        ((param.getHeightPlus() - textShape.getBounds().height) / 2) + textShape.getBounds().height
            + height);
    graphic.setColor(param.getTextColor());
    graphic.fill(textShape);
    graphic.setStroke(new BasicStroke(param.getStrokeNumber()));
    graphic.setColor(param.getStrokeColor());
    graphic.draw(textShape);

    ImageIO.write(sticker, param.getOutputFormat(),
        new File(param.getOutputPath() + param.getOutputName() + "." + param.getOutputFormat()));
  }

}
