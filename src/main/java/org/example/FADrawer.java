package org.example;

public interface FADrawer<T> {
    public String generateDot(T fa,String p);

    public String generatePng(String dotPath);
}
