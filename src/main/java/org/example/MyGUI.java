package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class MyGUI extends JFrame {
    private JLabel iconLabel;
    private JTextField urlField;
    private JButton loadButton;
    //private JLabel infoLabel;
    private JTextArea infoTextArea;

    private List<CurrencyInfo> currencyList;

    public MyGUI() {
        super("Currency Search");
        this.currencyList = loadAllCurrencyInfos();

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Input
        JPanel searchPanel = new JPanel(new FlowLayout());
        urlField = new JTextField(20);
        loadButton = new JButton("Search");
        searchPanel.add(urlField);
        searchPanel.add(loadButton);
        add(searchPanel, BorderLayout.NORTH);


        // left icon
        iconLabel = new JLabel("Image goes here", SwingConstants.CENTER);
        add(iconLabel,BorderLayout.WEST);

        // right info
        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);

        // enable automatic line wrapping
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);

        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);
        add(infoScrollPane, BorderLayout.EAST);

        //infoLabel = new JLabel("Info goes here", SwingConstants.CENTER);
        //add(infoLabel,BorderLayout.EAST);


        // Search button click event: search data and load image based on the input keyword
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String query = urlField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search keyword");
            return;
        }
        CurrencyInfo found = null;
        // Check if currencyCode or countryName contains the keyword
        for (CurrencyInfo info : loadAllCurrencyInfos()) {
            if (info.getCurrencyCode() != null && info.getCountryName() != null &&
                    (info.getCurrencyCode().toLowerCase().contains(query) ||
                            info.getCountryName().toLowerCase().contains(query))) {
                found = info;
                break;

            }
        }
        if (found != null) {
            //infoLabel.setText("找到: " + found.toString());
            infoTextArea.setText("FOUND: " + found.toString());

            loadIcon(found.getIcon());
        } else {
            //infoLabel.setText("未找到相关信息");
            infoTextArea.setText("No matching information found");
            iconLabel.setIcon(null);
            iconLabel.setText("Image will be displayed here");
        }
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
                iconLabel.setText("Unable to load image！");
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
