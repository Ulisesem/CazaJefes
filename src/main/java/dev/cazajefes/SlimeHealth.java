package dev.cazajefes;
final class SlimeHealth {
 /** Native size changes may refill health; restore the snapshot only after custom attributes. */
 static void resizePreserving(double before,Runnable resize,Runnable attributes,java.util.function.DoubleSupplier maximum,java.util.function.DoubleConsumer health){resize.run();attributes.run();health.accept(Math.min(before,maximum.getAsDouble()));}
}
