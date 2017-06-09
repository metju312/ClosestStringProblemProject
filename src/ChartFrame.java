import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class ChartFrame extends ApplicationFrame {
    public ChartFrame(String var1, XYSeriesCollection collection) {
        super(var1);
        JPanel var2 = createDemoPanel(collection);
        var2.setPreferredSize(new Dimension(500*2, 270*2));
        this.getContentPane().add(var2);
    }

    public static JPanel createDemoPanel(XYSeriesCollection collection) {
        NumberAxis var0 = new NumberAxis("Wartość parametru [1]");
        var0.setAutoRangeIncludesZero(false);
        NumberAxis var1 = new NumberAxis("Y");
        var1.setAutoRangeIncludesZero(false);
        XYSplineRenderer var2 = new XYSplineRenderer();
        XYPlot var3 = new XYPlot(collection, var0, var1, var2);
        var3.setBackgroundPaint(Color.lightGray);
        var3.setDomainGridlinePaint(Color.white);
        var3.setRangeGridlinePaint(Color.white);
        var3.setAxisOffset(new RectangleInsets(4.0D, 4.0D, 4.0D, 4.0D));
        JFreeChart var4 = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, var3, true);
        ChartUtilities.applyCurrentTheme(var4);
        ChartPanel var5 = new ChartPanel(var4);
        return var5;
    }

    public static void generateChart(XYSeriesCollection collection){
        ChartFrame var1 = new ChartFrame("Chart", collection);
        var1.pack();
        RefineryUtilities.centerFrameOnScreen(var1);
        var1.setVisible(true);
    }

    public static void main(String[] var0) {
        demo.XYSplineRendererDemo1a var1 = new demo.XYSplineRendererDemo1a("JFreeChart: ChartFrame.java");
        var1.pack();
        RefineryUtilities.centerFrameOnScreen(var1);
        var1.setVisible(true);
    }
}
