package dev.menace.utils.math;

public class Model {
    private final double[] smoothedData;
    private final double[] trends;
    private final double[] levels;
    private final double sse;

    public Model(double[] smoothedData, double[] trends, double[] levels, double sse) {
        this.smoothedData = smoothedData;
        this.trends = trends;
        this.levels = levels;
        this.sse = sse;
    }

    /**
     * Forecasts future values.
     *
     * @param size no of future values that you need to forecast
     * @return forecast data
     */
    double[] forecast(int size) {
        double[] forecastData = new double[size];
        for (int i = 0; i < size; i++) {
            forecastData[i] = levels[levels.length - 1] + (i + 1) * trends[trends.length - 1];
        }
        return forecastData;
    }

    public double[] getSmoothedData() {
        return smoothedData;
    }

    public double[] getTrend() {
        return trends;
    }

    public double[] getLevel() {
        return levels;
    }

    public double getSSE() {
        return sse;
    }
}
