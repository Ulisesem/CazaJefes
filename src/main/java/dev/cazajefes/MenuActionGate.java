package dev.cazajefes;
/** One queued action per menu prevents duplicate purchases from repeated packets. */
final class MenuActionGate {
 private boolean pending;
 boolean reserve() { if (pending) return false; pending=true; return true; }
 void release() { pending=false; }
}
