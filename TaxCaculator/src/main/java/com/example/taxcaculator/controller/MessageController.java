package com.example.taxcaculator.controller;

import com.example.taxcaculator.model.ThongBao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MessageController {
    Map<ThongBao,Boolean> mapMessage = new HashMap<>();

    @FXML
    private Button approveBtn;

    @FXML
    private AnchorPane lsMessage;
    public void initialize() throws SQLException {
        LoadData();
        approveBtn.setOnAction(event -> {
            if(AlertController.alert(Alert.AlertType.CONFIRMATION,"Xác nhận","Bạn có chắc chắn muốn thay đổi?")){
                mapMessage.forEach((thongBao, aBoolean) -> {
                    if(aBoolean!=null) {
                        if(aBoolean) {
                            thongBao.XacNhan();
                        }else{
                            thongBao.TuChoi();
                        }
                    }

                });
                try {
                    LoadData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void LoadData() throws SQLException {
        lsMessage.getChildren().clear();
        int i = 0;
        ResultSet rs = QueryController.getInstance().Query("Select noti_id, employee_id, manager_id, change_dependents from taxdb.notifications;");
        while (rs.next()){
            ThongBao tb = new ThongBao(rs.getString("noti_id"),rs.getString("employee_id")==null?rs.getString("manager_id"):rs.getString("employee_id"),rs.getInt("change_dependents"));
            if(tb.getIdNhanVien().startsWith("EMP")){
                ResultSet as = QueryController.getInstance().Query("select department_id from taxdb.employees where employee_id = '"+tb.getIdNhanVien()+"';");
                if(as.next()){
                    if(as.getString("department_id").equals(navBarController.phongBan.getId())){
                        mapMessage.put(tb,null);
                        createComponent(tb,i);
                        i++;
                    }
                }
            }else{
                if(rs.getString("manager_id").equals(navBarController.phongBan.getIdTruongPhong())){
                    mapMessage.put(tb,null);
                    createComponent(tb,i);
                    i++;
                }
            }

        }
    }
    private void createComponent(ThongBao tb, int i) {
        double layoutY = 50 + (100 * i);
        Text messageText = new Text();
        String nameTable = tb.getIdNhanVien().substring(0, 3).equals("EMP") ? "taxdb.employees" : "taxdb.managers";
        ResultSet rs = QueryController.getInstance().Query("select fullname from " + nameTable + ";");
        try {
            if (rs.next()) {
                messageText.setText(rs.getString("fullname") + " đã thay đổi số người phụ thuộc thành " + tb.getNewNPT());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        messageText.setFill(Paint.valueOf("#0965f4"));
        messageText.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        messageText.setLayoutX(40);
        messageText.setLayoutY(143 - 73 + layoutY); // Adjust layout dynamically

        // Chỉ hiển thị Agree/Disagree text ở dòng đầu tiên
        if (i == 0) {
            Text agreeTextNode = new Text("Đồng ý");
            agreeTextNode.setFill(Paint.valueOf("#0965f4"));
            agreeTextNode.setFont(Font.font("Arial Bold", 24));
            agreeTextNode.setLayoutX(961);
            agreeTextNode.setLayoutY(70 - 73 + layoutY);

            Text disagreeTextNode = new Text("Từ chối");
            disagreeTextNode.setFill(Paint.valueOf("#0965f4"));
            disagreeTextNode.setFont(Font.font("Arial Bold", 24));
            disagreeTextNode.setLayoutX(1160);
            disagreeTextNode.setLayoutY(70 - 73 + layoutY);

            lsMessage.getChildren().addAll(agreeTextNode, disagreeTextNode);
        }

        CheckBox agreeCheckBox = new CheckBox();
        agreeCheckBox.setLayoutX(984);
        agreeCheckBox.setLayoutY(111 - 73 + layoutY);
        agreeCheckBox.setPrefWidth(30);
        agreeCheckBox.setPrefHeight(30);
//        agreeCheckBox.setStyle("-fx-scale-x: 1.5; -fx-scale-y: 1.5;");

        CheckBox disagreeCheckBox = new CheckBox();
        disagreeCheckBox.setLayoutX(1167);
        disagreeCheckBox.setLayoutY(111 - 73 + layoutY);
        disagreeCheckBox.setPrefWidth(30);
        disagreeCheckBox.setPrefHeight(30);
//        disagreeCheckBox.setStyle("-fx-scale-x: 1.5; -fx-scale-y: 1.5;");

        // Thêm sự kiện để đảm bảo chỉ một checkbox được chọn
        agreeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                disagreeCheckBox.setSelected(false);
                mapMessage.put(tb,true);
            }
            if(!agreeCheckBox.isSelected() && !disagreeCheckBox.isSelected()){
                mapMessage.put(tb,null);
            }
        });

        disagreeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                agreeCheckBox.setSelected(false);
                mapMessage.put(tb,false);
            }
            if(!agreeCheckBox.isSelected() && !disagreeCheckBox.isSelected()){
                mapMessage.put(tb,null);
            }
        });

        lsMessage.getChildren().addAll(messageText, agreeCheckBox, disagreeCheckBox);

    }

}
