package eu.sloubi.yellowtext;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

	App app;

	public GlobalKeyListener(App app) {
		this.app = app;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		// not needed
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent event) {
		// not needed
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
		// WIN + N
		if (event.getModifiers() == NativeInputEvent.META_L_MASK && event.getKeyCode() == NativeKeyEvent.VC_N) {
			app.newNote();
		}
	}
}
