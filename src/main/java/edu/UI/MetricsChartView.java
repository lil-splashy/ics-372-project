package edu.UI;

import edu.ics372.OrderHandler;
import edu.ics372.SessionAnalytics;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class MetricsChartView {

    public static VBox createMetricsChartPane(OrderHandler orderHandler) {
        // ── Bar chart ────────────────────────────────────────────────────────

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(orderHandler.getOrdersImported(),  "Orders", "Imported");
        dataset.addValue(orderHandler.getOrdersStarted(),   "Orders", "Started");
        dataset.addValue(orderHandler.getOrdersCancelled(), "Orders", "Cancelled");
        dataset.addValue(orderHandler.getOrdersExported(),  "Orders", "Exported");

        JFreeChart chart = ChartFactory.createBarChart(
                "Order Summary", "Metric", "Count", dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(true);

        ChartViewer chartViewer = new ChartViewer(chart);
        chartViewer.setPrefSize(500, 300);
        chartViewer.addEventFilter(ScrollEvent.SCROLL, ScrollEvent::consume);
        chartViewer.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);

        // ── Session analytics panel ───────────────────────────────────────────

        SessionAnalytics analytics = SessionAnalytics.getInstance();
        long avgMs = analytics.getAverageCompletionTimeMs();
        String avgDisplay = avgMs > 0
                ? (avgMs / 1000) + "s"
                : "No completed orders yet";

        HBox statsRow = new HBox(40);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.setPadding(new Insets(16, 24, 16, 24));
        statsRow.getStyleClass().add("glass-bg");

        statsRow.getChildren().addAll(
                statTile("Tracked This Session", String.valueOf(analytics.getTotalTrackedCount())),
                statTile("Completed",             String.valueOf(analytics.getCompletedCount())),
                statTile("Avg Completion Time",   avgDisplay)
        );

        // ── Outer container ───────────────────────────────────────────────────

        VBox container = new VBox(12, chartViewer, statsRow);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPrefSize(600, 450);
        container.setPadding(new Insets(12));
        return container;
    }

    private static VBox statTile(String label, String value) {
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.WHITE);

        Label titleLabel = new Label(label);
        titleLabel.setFont(Font.font("IBM Plex Mono", 12));
        titleLabel.setTextFill(Color.web("#FFFFFF", 0.7));

        VBox tile = new VBox(4, valueLabel, titleLabel);
        tile.setAlignment(Pos.CENTER);
        return tile;
    }
}
