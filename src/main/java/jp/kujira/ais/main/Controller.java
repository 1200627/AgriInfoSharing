package jp.kujira.ais.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import jp.kujira.ais.information.Information;
import jp.kujira.ais.provider.Provider;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private final SimulatorModel simulatorModel = SimulatorModel.getInstance();

    private final ObservableList<Information> showingKnowHow = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<String, BigDecimal>> showingSeries = FXCollections.observableArrayList();

    @FXML
    private TextArea progressArea;

    @FXML
    private TableView<AISProperty> propertiesTable;
    @FXML
    private TableView<Provider> providersTable;
    @FXML
    private TableView<Information> knowHowTable;

    @FXML
    private TableColumn propertyNameColumn;
    @FXML
    private TableColumn valueColumn;
    @FXML
    private TableColumn providerIDColumn;
    @FXML
    private TableColumn khPointerColumn;
    @FXML
    private TableColumn canRequireColumn;
    @FXML
    private TableColumn earnedIncentiveColumn;
    @FXML
    private TableColumn contributionColumn;
    @FXML
    private TableColumn userButtonColumn;
    @FXML
    private TableColumn evaluationColumn;
    @FXML
    private TableColumn rarityColumn;
    @FXML
    private TableColumn usageCountColumn;
    @FXML
    private TableColumn diligentUsageCountColumn;
    @FXML
    private TableColumn freshnessColumn;
    @FXML
    private TableColumn providerWorthColumn;
    @FXML
    private TableColumn hiddenWorthColumn;
    @FXML
    private TableColumn userWorthColumn;
    @FXML
    private TableColumn diligentUserWorthColumn;
    @FXML
    private TableColumn isConsumedColumn;
    @FXML
    private TableColumn knowHowButtonColumn;

    @FXML
    private LineChart knowHowEvaluationChart;

    public void initialize(URL location, ResourceBundle resources) {
        // Bindings
        progressArea.textProperty().bind(simulatorModel.getLoggerProperty());
        simulatorModel.getLoggerProperty().addListener((observable, oldValue, newValue) -> {
            progressArea.selectPositionCaret(progressArea.getLength());
            progressArea.deselect();
        });

        propertiesTable.setEditable(true);
        propertiesTable.setItems(simulatorModel.getAISProperties());
        propertyNameColumn.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setCellFactory(arg0 -> new TextFieldTableCell<>(new DefaultStringConverter()));
        valueColumn.setOnEditCommit(event -> {
            TableColumn.CellEditEvent t = (TableColumn.CellEditEvent)event;
            if(!((String)t.getNewValue()).replaceAll("\\d", "").equals("")){
                ((AISProperty) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue((String)t.getOldValue());
                propertiesTable.refresh();
                return;
            }
            ((AISProperty) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue((String)t.getNewValue());
        });
        propertiesTable.getColumns().setAll(propertyNameColumn, valueColumn);

        providersTable.setEditable(true);
        providersTable.setItems(simulatorModel.getProviders());
        providerIDColumn.setCellValueFactory(new PropertyValueFactory<>("providerID"));
        khPointerColumn.setCellValueFactory(new PropertyValueFactory<>("knowHowPointer"));
        canRequireColumn.setCellValueFactory(new PropertyValueFactory<>("canRequire"));
        earnedIncentiveColumn.setCellValueFactory(new PropertyValueFactory<>("earnedIncentive"));
        contributionColumn.setCellValueFactory(new PropertyValueFactory<>("contribution"));
        userButtonColumn.setCellValueFactory(new PropertyValueFactory<>("providerID"));
        userButtonColumn.setCellFactory(p -> new UserButtonCell());
        providersTable.getColumns().setAll(providerIDColumn, khPointerColumn, canRequireColumn, earnedIncentiveColumn, contributionColumn, userButtonColumn);

        knowHowTable.setEditable(true);
        knowHowTable.setItems(showingKnowHow);
        evaluationColumn.setCellValueFactory(new PropertyValueFactory<>("evaluation"));
        rarityColumn.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        usageCountColumn.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
        diligentUsageCountColumn.setCellValueFactory(new PropertyValueFactory<>("diligentUsageCount"));
        freshnessColumn.setCellValueFactory(new PropertyValueFactory<>("freshness"));
        providerWorthColumn.setCellValueFactory(new PropertyValueFactory<>("providerWorth"));
        hiddenWorthColumn.setCellValueFactory(new PropertyValueFactory<>("hiddenWorth"));
        userWorthColumn.setCellValueFactory(new PropertyValueFactory<>("userWorth"));
        diligentUserWorthColumn.setCellValueFactory(new PropertyValueFactory<>("diligentUserWorth"));
        isConsumedColumn.setCellValueFactory(new PropertyValueFactory<>("isConsumed"));
        knowHowButtonColumn.setCellValueFactory(new PropertyValueFactory<>("series"));
        knowHowButtonColumn.setCellFactory(p -> new KnowHowButtonCell());
        knowHowTable.getColumns().setAll(evaluationColumn, rarityColumn, usageCountColumn, diligentUsageCountColumn, freshnessColumn, providerWorthColumn, hiddenWorthColumn, userWorthColumn, diligentUserWorthColumn, isConsumedColumn, knowHowButtonColumn);

        knowHowEvaluationChart.setData(getShowingSeries());

        // Initialization
        simulatorModel.initialize();
    }

    @FXML
    private void onStart(){
        simulatorModel.start();
    }

    @FXML
    private void onStop(){
        simulatorModel.stop();
    }

    @FXML
    private void onReset(){ simulatorModel.initialize(); }

    @FXML
    private void onProgressAreaDown(){
        progressArea.setScrollTop(Double.MAX_VALUE);
    }

    public ObservableList<Information> getShowingKnowHow(){ return showingKnowHow; }
    public ObservableList<XYChart.Series<String, BigDecimal>> getShowingSeries(){ return showingSeries; }

    private class UserButtonCell extends TableCell<Provider, Integer>{
        private Integer id;
        private final Button cellButton = new Button("Show know-how");

        UserButtonCell() {
            cellButton.setOnAction(t -> {
                getShowingKnowHow().setAll(SimulatorModel.getInstance().getProviders().get(id-1).getKnowHow());
            });
        }

        @Override
        protected void updateItem(Integer t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                id = t;
                setGraphic(cellButton);
            }
        }
    }

    private class KnowHowButtonCell extends TableCell<Information, XYChart.Series<String, BigDecimal>>{
        private XYChart.Series<String, BigDecimal> series;
        private final Button cellButton = new Button("Show chart");

        KnowHowButtonCell() {
            cellButton.setOnAction(t -> {
                getShowingSeries().setAll(series);
            });
        }

        @Override
        protected void updateItem(XYChart.Series<String, BigDecimal> t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                series = t;
                setGraphic(cellButton);
            }
        }
    }
}
