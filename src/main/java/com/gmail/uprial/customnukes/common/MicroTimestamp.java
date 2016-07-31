package com.gmail.uprial.customnukes.common;

import java.text.SimpleDateFormat;

public enum MicroTimestamp {
    INSTANCE ;

   private final long startDate ;
   private final long startNanoseconds ;
   private final SimpleDateFormat dateFormat ;

   MicroTimestamp() {
       startDate = System.currentTimeMillis() ;
       startNanoseconds = System.nanoTime() ;
       dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") ;
   }

   public String get() {
       long microSeconds = (System.nanoTime() - startNanoseconds) / 1000 ;
       long date = startDate + (microSeconds / 1000) ;

       return dateFormat.format(date) + String.format("%03d", microSeconds % 1000) ;
   }
}
