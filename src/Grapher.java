import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * A class for rendering graphs.
 */
public class Grapher {

    /**
     * The height, in pixels, of the graph to be generated.
     * A minimum value of
     */
    private int height;

    /**
     * The width, in pixels, of the graph to be generated.
     * A minimum value of
     */
    private int width;

    /**
     * Value of y-coordinate at the bottom edge of the graph.
     * Must be less than yMax. Considered a "window setting"
     * because it defines the range of the graph.
     */
    private long yMin;

    /**
     * Value of y-coordinate at the top edge of the graph.
     * Must be greater than yMin. Considered a "window setting"
     * because it defines the range of the graph.
     */
    private long yMax;

    /**
     * Value of x-coordinate at the left edge of the graph.
     * Must be less than xMax. Considered a "window setting"
     * because it defines the range of the graph.
     */
    private long xMin;

    /**
     * Value of x-coordinate at the right edge of the graph.
     * Must be greater than xMin. Considered a "window setting"
     * because it defines the range of the graph.
     */
    private long xMax;

    /**
     * Whether or not to draw grid lines in the graph's background,
     * behind the axis. Grid lines are drawn vertically and
     * horizontally across the graph. Note that no lines are drawn
     * along the edges of the graph.
     */
    private boolean drawGridlines;

    /**
     * Relative space between gridlines, if drawGridLines = true.
     * On the x-axis, the absolute space (in pixels) is determined
     * using the formula width * gridLineSpacing, and on the y-axis
     * is determined by the formula height * gridLineSpacing.
     * Because ticks are only drawn where there are grid lines,
     * this setting also determines the spacing between ticks.
     */
    private float gridLineSpacing;

    /**
     * Relative thickness, or width, of each grid line, if
     * drawGridLines = true. The absolute thickness (in pixels)
     * of a grid line is determined using the formula
     * width * gridLineThickness. Note that height is not used
     * in this calculation.
     * This setting also sets the thickness of ticks, if
     * drawTicks = true.
     */
    private float gridLineThickness;

    /**
     * Color of grid lines. // todo: specify default values
     */
    private Color gridLineColor;

    /**
     * BasicStroke used when drawing grid lines with the Graphics
     * API
     */
    private BasicStroke gridLineStroke;

    /**
     * Whether or not to draw ticks along the x- and y-axis.
     *
     */
    private boolean drawTicks; // todo: add label ticks option

    private BasicStroke tickStroke;

    private float tickLength;

    private boolean labelTicks;

    /**
     * Background color of graph.
     */
    private Color backgroundColor;

    /**
     * Color of axis, and ticks drawn along axis
     * (if drawTicks = true).
     */
    private Color axisColor;

    /**
     * BasicStroke style used for drawing the axis with the
     * Graphics API
     */
    private BasicStroke axisStroke;

    /**
     * Relative width of axis compared to width of graph.
     * The absolute value in pixels is calculated with the
     * formula width * axisWidth. Note that height is not
     * used in this calculation.
     */
    private float axisWidth;

    private BasicStroke plotStroke; // todo: is plotWidth redundant if plotStroke already sets width of pixels to draw?

    private float plotWidth;

    private Color plotColor;

    // todo: check that range makes sense; Add documentation
    // todo: allow user to set BasicStrokes for components
    // todo: set gridlines using relative value (e.g. 10% of width)
    /**
     * Default constructor. Sets all values to default values.
     */
    public Grapher() {
        yMin = -10;
        yMax = 10;
        xMin = -10;
        xMax = 10;

        drawGridlines = true;
        gridLineSpacing = 0.10f;
        gridLineThickness = 0.01f;
        gridLineColor = Color.BLUE;

        drawTicks = true;
        tickLength = 0.10f;
        labelTicks = false;

        backgroundColor = Color.WHITE;

        axisColor = Color.BLACK;
        axisWidth = 0.02f;

        plotWidth = 0.01f;
        plotColor = Color.BLUE;
    }

    /**
     * Draws the grid, or background, for the function/points
     * to be plotted on graph. Fills in background color
     * of graph, draws grid lines (if drawGridLines = true),
     * draws axis, and draws ticks (if drawTicks = true) using
     * styles.
     * @param blank_image blank BufferedImage for grid to be drawn on
     * @return blank_image with grid drawn on it
     */
    public BufferedImage drawGrid(BufferedImage blank_image) { // todo: either pass as Graphics or createGraphics here
        setWidthHeight(blank_image);

        if(validateSettings()) {
            Graphics2D graphics = blank_image.createGraphics();
            graphics = drawBackground(graphics);
            if (drawGridlines)
                graphics = drawGridLines(graphics);
            graphics = drawAxis(graphics);
            if (drawTicks)
                graphics = drawTicks(graphics);
        }

        return blank_image;
    }

    /**
     * Sets background color of graph using the backgroundColor
     * field.
     * @param graph Graphics2D object of graph being drawn
     * @return
     */
    private Graphics2D drawBackground(Graphics2D graph) {
        graph.setBackground(backgroundColor);
        return graph;
    }

    /**
     * Uses class-defined settings to draw horizontal and vertical
     * grid lines on the BufferedImage.
     * @param graph Graphics2D object of graph being drawn
     * @return
     */
    private Graphics2D drawGridLines(Graphics2D graph) {
        /* First, calculate distance between gridlines using image
         * height and width and range to display. */
        int spacing_x = (int) (width * gridLineSpacing);
        int spacing_y = (int) (height * gridLineSpacing);

        if(gridLineStroke != null)
            graph.setStroke(gridLineStroke);
        else
            graph.setStroke(new BasicStroke(width * gridLineThickness));

        graph.setColor(gridLineColor);

        /* Draw vertical grid lines */
        for(int i = spacing_x; i < width; i += spacing_x) {
            graph.draw(new Line2D.Double(i, 0, i, height));
        }

        /* Draw horizontal grid lines */
        for(int i = spacing_y; i < height; i += spacing_y) {
            graph.draw(new Line2D.Double(0, i, 0, width));
        }

        return graph;
    }

    /**
     *
     * @param graph
     * @return
     */
    private Graphics2D drawAxis(Graphics2D graph) { // todo: axis may not be centered
        if(axisStroke != null)
            graph.setStroke(axisStroke);
        else
            graph.setStroke(new BasicStroke(width * axisWidth));

        graph.setColor(axisColor);

        graph.draw(new Line2D.Double(width / 2, 0, width / 2, height));
        graph.draw(new Line2D.Double(0, height / 2, width, height / 2));

        return graph;
    }

    /**
     * Draws ticks along x- and y-axis.
     * Uses gridLineSpacing to determine space between ticks,
     * axisColor to determine color of ticks that are drawn,
     * and tickStroke to style the ticks.
     * @param graph
     * @return
     */
    private Graphics2D drawTicks(Graphics2D graph) {
        if(tickStroke != null)
            graph.setStroke(tickStroke);
        else
            graph.setStroke(new BasicStroke(width * gridLineThickness));

        graph.setColor(axisColor);

        /* Calculate spacing along x- and y-axis between individual
         * ticks */
        int spacing_x = (int) (width * gridLineSpacing);
        int spacing_y = (int) (height * gridLineSpacing);

        /* Calculate length of each tick based on image width */
        int tick_length = (int) (width * tickLength);

        /* Calculate x-start and x-end coordinates of ticks on the y-axis */
        int tick_start = width / 2 - tick_length / 2;
        int tick_end = width / 2 + tick_length / 2;

        for(int i = spacing_x; i < width; i += spacing_x) { // todo: refactoring
            graph.draw(new Line2D.Double(tick_start, i, tick_end, i));
        }

        /* Calculate y-start and y-end coordinates of ticks on the x-axis */
        tick_start = height / 2 + tick_length / 2;
        tick_end = height / 2 - tick_length / 2;

        for(int i = spacing_y; i < height; i += spacing_y) {
            graph.draw(new Line2D.Double(i, tick_start, i, tick_end));
        }

        return graph;
    }

    /**
     * "y = " or "f(x)" function used for graphing user-specified
     * function. By default, returns x.
     * To change this the user must enter their own expression.
     * @param x x-value used to calculate f(x)
     * @return f(x) using expression
     */
    public double calculate(double x) {
        return x;
    }

    /**
     * Checks to make sure that all settings are valid.
     * xMax must be greater than xMin and yMax must
     * be greater than yMin. Height and Width must
     * be greater than zero. Also makes sure that all
     * settings relative to height and width are less
     * than or equal to one.
     * @return whether all settings are valid
     * @throws IndexOutOfBoundsException - if settings break the rules
     */
    private boolean validateSettings() throws IndexOutOfBoundsException { // todo: improve
        if(xMax <= xMin)
            throw new IndexOutOfBoundsException("xMax cannot be less than or equal to xMin");
        if(yMax <= yMin)
            throw new IndexOutOfBoundsException("yMax cannot be less than or equal to yMin");
        if(height <= 0)
            throw new IndexOutOfBoundsException("height cannot be less than or equal to zero");
        if(width <= 0)
            throw new IndexOutOfBoundsException("width cannot be less than or equal to zero");
        //if(gridLineStroke.getLineWidth() > width || gridLineStroke.getLineWidth() > height)
        //    throw new IndexOutOfBoundsException("gridLineStroke LineWidth cannot be larger than width or height");
        if(gridLineSpacing > 1)
            throw new IndexOutOfBoundsException("gridLineSpacing cannot be greater than one");
        if(gridLineThickness > 1)
            throw new IndexOutOfBoundsException("gridLineThickness cannot be greater than one");
        if(tickLength > 1)
            throw new IndexOutOfBoundsException("tickLength cannot be greater than one");
        if(axisWidth > 1)
            throw new IndexOutOfBoundsException("axisWidth cannot be greater than one");
        if(plotWidth > 1)
            throw new IndexOutOfBoundsException("plotWidth cannot be greater than one");
        return true;
    }

    /**
     * Renders graph from scratch and draws and emphasizes specified
     * points on the graph (as long as they are in the graph's range).
     * Coordinates of points to draw on graph are passed in 2d array
     * where double[0][index] gives the x-coordinate of a point and
     * double[1][index] gives the coresponding y-coordinate. Points are
     * drawn as circles using plotStroke, plotWidth, and plotColor
     * properties. // todo: properties or fields?
     * @param points x- and y-values of points to plot and emphasize // todo: update
     * @return
     * @throws IndexOutOfBoundsException - if double[0] is a different
     * size than double[1]
     */
    public BufferedImage drawGraph(BufferedImage blank_image, double[][] points) throws IndexOutOfBoundsException {
        setWidthHeight(blank_image);

        if(validateSettings()) {
            Graphics2D graph = blank_image.createGraphics();
            drawBackground(graph);
            for(int i = 0; i < points[0].length; i++) {
                plotPoint(graph, points[0][i], points[1][i]);
            }
        }

        return blank_image;
    }

    /**
     * Draws graph on the specified grid using window values specified
     * (Y_MIN, Y_MAX, X_MIN, X_MAX).
     *
     * Use values to specify individual points to plot, where first index
     * is x-coordinate and second index is y-coordinate.
     *
     * @param grid
     * @param points
     * @return
     */
    public BufferedImage drawGraphOnGrid(BufferedImage grid, double[][] points) throws IndexOutOfBoundsException {
        setWidthHeight(grid);

        if(validateSettings()) {
            Graphics2D graph = grid.createGraphics();
            for(int i = 0; i < points[0].length; i++) {
                plotPoint(graph, points[0][i], points[1][i]);
            }
        }

        return grid;
    }

    /**
     * drawGraph() uses calculate and graph range
     * @return
     */
    public BufferedImage drawGraph(BufferedImage blank_image, double rangeLow, double rangeHigh) { // todo: allow setting ranges
        setWidthHeight(blank_image);
        drawBackground(blank_image.createGraphics());
        return drawGraphOnGrid(blank_image, rangeLow, rangeHigh);
    }

    public BufferedImage drawGraphOnGrid(BufferedImage grid, double rangeLow, double rangeHigh) { // todo: exclusive v. inclusive points
        setWidthHeight(grid);
        Graphics2D graph = grid.createGraphics();

        float units_per_pxl = (xMax - xMin) / width;

        for(double i = rangeLow; i <= rangeHigh; i += units_per_pxl) {
            plotPoint(graph, i, calculate(i));
        }

        return grid;
    }

    /**
     * Converts xCoordinate and yCoordinate to a coordinate
     * on the userspace of graph and draws a filled circle
     * of radius (width * graphWidth) / 2 and color graphColor. // todo: missing anything?
     * Note: if coordinates are out of graph's range they will
     * not be plotted.
     * @param graph Graphics2D object of graph being drawn on
     * @param xCoordinate x-coordinate of point being plotted
     * @param yCoordinate y-coordinate of point being plotted
     * @return
     */
    private Graphics2D plotPoint(Graphics2D graph, double xCoordinate, double yCoordinate) { // todo: height and width shouldn't be class variables. They should be specified when creating each graph individually
        /* Check to see if coordinates fall in graph range */
        if((xCoordinate >= xMin && xCoordinate <= xMax) && (yCoordinate >= yMin && yCoordinate <= yMax)) {
            /* Convert number coordinates to a coordinate on graph's user space */
            int[] px_coordinates = coordinateToPixel(xCoordinate, yCoordinate);

            if(plotStroke != null)
                graph.setStroke(plotStroke); // todo: test
            graph.setColor(plotColor);

            /* Draw a point with diameter = plotWidth at specified coordinates in userspace */
            graph.fillOval(px_coordinates[0], px_coordinates[1], (int) (plotWidth / 2), (int) (plotWidth / 2));
        }

        return graph;
    }

    /**
     * Converts coordinates of a point on the graph to coordinates of // todo: make sure height and width aren't greater than int limit (32765)
     * that point on the userspace of the Graphics2D object where drawing
     * takes place.
     * Uses window settings and height and width fields. Errors will occur
     * if these settings are not up to date.
     * @param xCoordinate
     * @param yCoordinate
     * @return int[] where int[0] is x-coordinate and int[1] is y-coordinate of
     * point's location in userspace
     */
    private int[] coordinateToPixel(double xCoordinate, double yCoordinate) {
        /* Calculate "range" covered on each axis */
        double x_range = xMax - xMin;
        double y_range = yMax - yMin;

        /* Calculate pixels per unit */
        double x_px_unit = width / x_range;
        double y_px_unit = height / y_range;

        return new int[] {(int) ((xCoordinate - xMin) * x_px_unit), (int) ((yCoordinate - yMin) * y_px_unit)};
    }

    //private long pixelToCoordinate(int xCoordinate, int yCoordinate) {

    //}

    /**
     * Sets width and height fields based on specifications of
     * to_draw.
     * This is used to keep the width and height fields up to date
     * with the BufferedImage being drawn on.
     * @param to_draw BufferedImage upon which the graph will be drawn
     */
    private void setWidthHeight(BufferedImage to_draw) {
        width = to_draw.getWidth();
        height = to_draw.getHeight();
    }

    //public Graphics labelPoint(double xCoordinate, double yCoordinate) {

    //}
}
