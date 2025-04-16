package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MyGUI extends JFrame {
    private JTextField urlField;
    private JButton loadButton;
    private JLabel iconLabel;
    private JLabel currencyCodeLabel;
    private JLabel currencyNameLabel;
    private JLabel countryCodeLabel;
    private JLabel countryNameLabel;
    private JLabel exchangeRateLabel;
    private JPanel graphPanel;

    public MyGUI() {
        super("Currency Search");

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout());
        urlField = new JTextField(20);
        loadButton = new JButton("Search");
        JLabel hintLabel = new JLabel("Enter country name or currency code");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        searchPanel.add(urlField);
        searchPanel.add(loadButton);
        searchPanel.add(hintLabel);
        add(searchPanel, BorderLayout.NORTH);

        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS));

        JPanel infoPanelGraph = new JPanel(new FlowLayout(FlowLayout.LEFT));

        graphPanel = new JPanel();
        graphPanel.setPreferredSize(new Dimension(400, 200));
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        graphPanel.add(new JLabel("Graph Here"));

        iconLabel = new JLabel("Image here", SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(100, 100));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        infoPanelGraph.add(graphPanel);
        infoPanelGraph.add(iconLabel);

        JPanel infoTextPanel = new JPanel();
        infoTextPanel.setLayout(new BoxLayout(infoTextPanel, BoxLayout.Y_AXIS));
        infoTextPanel.add(new JLabel("Currency Info:"));

        currencyCodeLabel = new JLabel("Currency Code: ");
        currencyNameLabel = new JLabel("Currency Name: ");
        countryCodeLabel = new JLabel("Country Code: ");
        countryNameLabel = new JLabel("Country Name: ");
        exchangeRateLabel = new JLabel("Exchange Rate: ");

        infoTextPanel.add(currencyCodeLabel);
        infoTextPanel.add(currencyNameLabel);
        infoTextPanel.add(countryCodeLabel);
        infoTextPanel.add(countryNameLabel);
        infoTextPanel.add(exchangeRateLabel);

        mainCenterPanel.add(infoPanelGraph);
        mainCenterPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainCenterPanel.add(infoTextPanel);
        add(mainCenterPanel, BorderLayout.CENTER);

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }

    /**
     * Performs the search based on the input currency code or country name and updates the UI with the results.
     */
    private void performSearch() {
        String query = urlField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search keyword");
            return;
        }

        CurrencyInfo found = null;
        for (CurrencyInfo info : loadAllCurrencyInfos()) {
            if (info.getCurrencyCode() != null && info.getCountryName() != null &&
                    (info.getCurrencyCode().toLowerCase().contains(query) ||
                            info.getCountryName().toLowerCase().contains(query))) {
                found = info;
                break;
            }
        }

        if (found != null) {
            currencyCodeLabel.setText("Currency Code: " + found.getCurrencyCode());
            currencyNameLabel.setText("Currency Name: " + found.getCurrencyName());
            countryCodeLabel.setText("Country Code: " + found.getCountryCode());
            countryNameLabel.setText("Country Name: " + found.getCountryName());
            exchangeRateLabel.setText("Exchange Rate: " + found.getExchangeRate());
            loadIcon(found.getIcon());
            drawExchangeRateChart(found.getCurrencyCode());
        } else {
            currencyCodeLabel.setText("Currency Code: ");
            currencyNameLabel.setText("Currency Name: ");
            countryCodeLabel.setText("Country Code: ");
            countryNameLabel.setText("Country Name: ");
            exchangeRateLabel.setText("Exchange Rate: ");
            iconLabel.setIcon(null);
            iconLabel.setText("Image goes here");
            graphPanel.removeAll();
            graphPanel.add(new JLabel("Graph Here"));
            graphPanel.revalidate();
            graphPanel.repaint();
        }
    }


    /**
     * Draws the exchange rate chart for a specific currency.
     * This method was inspired by the XYLineChartTest.java example from JFreeChart:
     * https://github.com/jfree/jfreechart/blob/master/src/test/java/org/jfree/chart/XYLineChartTest.java
     *
     * @param currencyCode the currency code for which the chart should be drawn
     */
    private void drawExchangeRateChart(String currencyCode) {
        XYSeries series = new XYSeries(currencyCode);
        String sql = "SELECT exchange_rate, timestamp FROM exchange_rate_history WHERE currency_code = ? ORDER BY timestamp ASC LIMIT 30";

        try (Connection conn = DatabaseConnector.getAWSConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currencyCode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double rate = rs.getDouble("exchange_rate");
                Timestamp ts = rs.getTimestamp("timestamp");
                long time = ts.getTime();
                series.add(time, rate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Exchange Rate History for " + currencyCode,
                "Time",
                "Rate",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        XYPlot plot = chart.getXYPlot();

        // X axis
        DateAxis domainAxis = new DateAxis("Time");
        domainAxis.setDateFormatOverride(new SimpleDateFormat("MM-dd"));
        plot.setDomainAxis(domainAxis);

        // Y axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeMinimumSize(0.000001);  // 可视化用精度调节点
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#.######"));

        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));

        // Label
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        renderer.setDefaultItemLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        renderer.setDefaultShapesVisible(true);
        renderer.setDrawSeriesLineAsPath(true);

        plot.setRenderer(renderer);

        graphPanel.removeAll();
        graphPanel.add(new ChartPanel(chart));
        graphPanel.revalidate();
        graphPanel.repaint();
    }


    private void loadIcon(String iconURL) {
        try {
            URL url = new URL(iconURL);
            BufferedImage image = ImageIO.read(url);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                iconLabel.setIcon(icon);
                iconLabel.setText("");
            } else {
                iconLabel.setIcon(null);
                iconLabel.setText("Unable to load image!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            iconLabel.setText("Load failed: " + e.getMessage());
        }
    }

    private List<CurrencyInfo> loadAllCurrencyInfos() {
        List<CurrencyInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM exchange_rates";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                CurrencyInfo info = new CurrencyInfo();
                info.setCurrencyCode(rs.getString("currency_code"));
                info.setCurrencyName(rs.getString("currency_name"));
                info.setCountryCode(rs.getString("country_code"));
                info.setCountryName(rs.getString("country_name"));
                info.setStatus(rs.getString("status"));
                info.setAvailableFrom(rs.getString("available_from"));
                info.setAvailableUntil(rs.getString("available_until"));
                info.setIcon(rs.getString("icon"));
                info.setExchangeRate(rs.getDouble("exchange_rate"));
                list.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
