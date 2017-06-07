package jp.kujira.ais.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Kujira on 2017/04/28.
 */
public class AISProperty {
    private StringProperty propertyName;
    private StringProperty value;

    public AISProperty(final String propertyName, final String value){
        this.propertyName = new SimpleStringProperty(propertyName);
        this.value = new SimpleStringProperty(value);
    }

    public StringProperty propertyNameProperty(){ return propertyName; }
    public StringProperty valueProperty(){ return value; }

    public void setValue(final String value){
        this.value.setValue(value);
    }
}
