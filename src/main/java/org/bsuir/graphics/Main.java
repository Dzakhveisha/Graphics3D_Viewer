package org.bsuir.graphics;

import org.bsuir.graphics.presentation.MainFrame;

public class Main {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        MainFrame frame = new MainFrame();
        frame.start();
    }

    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler
    {
        public void uncaughtException(Thread thread, Throwable thrown)
        {
            System.out.println("lol");
            // TODO handle your Exception here
        }
    }
}