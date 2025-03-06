package graphics;

public class Filter {
    private float hue;
    private float saturation;
    private float brightness;

    public Filter(float hue, float saturation, float brightness) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public float getHue() { return hue; }
    public float getSaturation() { return saturation; }
    public float getBrightness() { return brightness; }
}