package model;

import model.CommandType;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    CommandType getType();
}

