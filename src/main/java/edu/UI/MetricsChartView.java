package edu.UI;

import edu.ics372.OrderHandler;
import edu.ics372.SessionAnalytics;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class MetricsChartView {

    //Metrics chart for imported, exported, and deleted orders.
    public static StackPane createMetricsChartPane(SessionAnalytics session){
        double chartViewWidth = 500;
        double chartViewHeight = 300;
        double chartPaneWidth = 600;
        double chartPaneHeight = 400;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(session.getOrdersImported(), "Orders", "Imported");
        dataset.addValue(session.getOrdersStarted(), "Orders", "Started");
        dataset.addValue(session.getOrdersCancelled(), "Orders", "Cancelled");
        dataset.addValue(session.getOrdersExported(), "Orders", "Exported");
        dataset.addValue(session.getOrdersCompleted(), "Orders", "Completed");

        JFreeChart chart = ChartFactory.createBarChart(
                "Order Summary",
                "Metric",
                "Count",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(true);

        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setPrefWidth(chartViewWidth);
        chartViewer.setPrefHeight(chartViewHeight);
        chartViewer.addEventFilter(ScrollEvent.SCROLL, ScrollEvent::consume);
        chartViewer.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);

        StackPane chartPane = new StackPane(chartViewer);
        chartPane.setPrefSize(chartPaneWidth, chartPaneHeight);
        return chartPane;
    }


}
