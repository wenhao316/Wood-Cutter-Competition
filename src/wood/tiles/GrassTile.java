package wood.tiles;

import wood.graphics.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public abstract class GrassTile extends Tile {
    private static final int MAX_BOARD_SIZE_FOR_GRADIENT = 30;

    protected GrassTile(Point tileLocation) {
        super(tileLocation);
    }

    @Override
    protected List<Image> getImageOverlays(ImageManager imageManager) {
        // The background forms a gradient of green, get the color of this part of the gradient
        String locationBackgroundImageName = this.location.x + "_" + location.y + "_background";
        if(!imageManager.hasScaledImageWithName(locationBackgroundImageName)) {
            int greenProportion = location.x - location.y; // Make the background a gradient of green
            if(greenProportion > MAX_BOARD_SIZE_FOR_GRADIENT) {
                greenProportion = 0; // At board sizes this big it honestly doesn't matter
            }

            Color greenShade = new Color(0, greenProportion * 3 + 162, 0);
            ImageIcon backgroundImage = imageManager.generateBackgroundImageIcon(greenShade);
            imageManager.addImageFromIcon(locationBackgroundImageName, backgroundImage);
        }

        // Get the randomly generated grass overlay
        String locationGrassImageName = this.location.x + "_" + location.y + "_grass";
        if(!imageManager.hasScaledImageWithName(locationGrassImageName)) {
            ImageIcon randomGrass = createRandomGrassOverlay(imageManager.getImageWidth(), imageManager.getImageHeight());
            imageManager.addImageFromIcon(locationGrassImageName, randomGrass);
        }

        List<Image> overlays = new LinkedList<>();
        Image grassBackground = imageManager.getScaledImage(locationBackgroundImageName);
        Image randomGrassOverlay = imageManager.getScaledImage(locationGrassImageName);

        overlays.add(grassBackground);
        overlays.add(randomGrassOverlay);
        return overlays;
    }

    private ImageIcon createRandomGrassOverlay(int imageWidth, int imageHeight) {
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageBrush = bufferedImage.createGraphics();

        for(int i = 0; i < 50; i++) {
            Color randomGreenShade = new Color(0, (int) (Math.random() * 100) + 100, 0);
            imageBrush.setColor(randomGreenShade);

            int randomX = (int) (Math.random() * imageWidth);
            int randomY = (int) (Math.random() * imageHeight);
            imageBrush.fillRect(randomX, randomY, 2, 4);
        }

        return new ImageIcon(bufferedImage);
    }
}
