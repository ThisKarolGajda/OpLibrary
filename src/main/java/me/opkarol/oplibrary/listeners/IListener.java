package me.opkarol.oplibrary.listeners;

import java.io.Serializable;

public interface IListener extends Serializable {

    void runListener();

    void stopListener();

}