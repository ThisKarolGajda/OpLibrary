package me.opkarol.oplibrary.listeners;

import me.opkarol.oplibrary.injection.IgnoreInject;

import java.io.Serializable;

@IgnoreInject
public interface IListener extends Serializable {

    void runListener();

    void stopListener();

}