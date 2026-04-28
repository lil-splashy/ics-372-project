package edu.UI;

import edu.ics372.OrderHandler;
import javafx.scene.layout.StackPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

public class MetricsChartView {

    //Metrics chart for imported, exported, and deleted orders.
    public static StackPane createMetricsChartPane(OrderHandler orderHandler){
        double chartViewWidth = 500;
        double chartViewHeight = 300;
        double chartPaneWidth = 600;
        double chartPaneHeight = 400;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(orderHandler.getOrdersImported(), "Orders", "Imported");
        dataset.addValue(orderHandler.getOrdersStarted(), "Orders", "Started");
        dataset.addValue(orderHandler.getOrdersCancelled(), "Orders", "Cancelled");
        dataset.addValue(orderHandler.getOrdersExported(), "Orders", "Exported");

        JFreeChart chart = ChartFactory.createBarChart(
                "Order Summary",
                "Metric",
                "Count",
                dataset
        );

        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setPrefWidth(chartViewWidth);
        chartViewer.setPrefHeight(chartViewHeight);

        StackPane chartPane = new StackPane(chartViewer);
        chartPane.setPrefSize(chartPaneWidth, chartPaneHeight);
        return chartPane;
    }


}
