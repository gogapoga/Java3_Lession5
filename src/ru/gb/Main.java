package ru.gb;

import java.util.concurrent.*;

public class Main {
    public static final int CARS_COUNT = 4;
    public static final CountDownLatch cdl1 = new CountDownLatch(CARS_COUNT); //синхронизация готовности
    public static final CountDownLatch cdl2 = new CountDownLatch(1);//синхронизация начала гонки
    public static final Semaphore smp = new Semaphore(CARS_COUNT/2, true); //для туннеля, важен порядок
    public static Boolean win = false; //обработка победы
    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        ExecutorService service = Executors.newFixedThreadPool(CARS_COUNT);
        for (int i = 0; i < cars.length; i++) service.execute(cars[i]);
        try {
            cdl1.await(); //ждем пока все авто будут готовы
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        cdl2.countDown(); //запускаем старт (без этой строчки иногда  "Гонка началась" выходило позже чем первые участники проезжали первый участок)
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); //ждем финиша всех
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}
