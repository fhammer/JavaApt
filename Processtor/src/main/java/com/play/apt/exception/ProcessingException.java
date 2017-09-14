package com.play.apt.exception;

import javax.lang.model.element.Element;

/**
 * Created by fuzh2 on 2017/9/14.
 */
public class ProcessingException extends Exception {

    Element element;

    public ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}