package me.cometkaizo.screen;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ImageSource {
    protected Image image;
    protected Future<? extends Image> imageSup;

    public ImageSource(Image image) {
        this.image = image;
    }
    public ImageSource(Future<? extends Image> imageSup) {
        this.imageSup = imageSup;
    }

    public Image image() {
        if (image == null && imageSup != null) {
            if (imageSup.isDone()) {
                try {
                    image = imageSup.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalStateException("Failed to load image", e);
                }
                imageSup = null;
            }
        }
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        this.imageSup = null;
    }

    public void setImage(Future<Image> imageSup) {
        this.image = null;
        this.imageSup = imageSup;
    }

    public boolean isVisible() {
        return image() != null;
    }

    public boolean isLoading() {
        return imageSup != null && !imageSup.isDone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageSource that = (ImageSource) o;
        return Objects.equals(image, that.image) && Objects.equals(imageSup, that.imageSup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, imageSup);
    }

    @Override
    public String toString() {
        return "ImageSource{" +
                "image=" + image +
                ", imageSup=" + imageSup +
                '}';
    }
}
