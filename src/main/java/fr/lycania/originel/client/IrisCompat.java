package fr.lycania.originel.client;

import java.lang.reflect.Method;

/**
 * Soft/optional dependency on Iris (shader loader), via reflection so the mod
 * still loads fine on clients without it installed. Complementary Unbound and
 * other shaderpacks fully replace the vanilla sky/fog rendering pipeline in
 * their own fragment shaders, which don't read the fog color we set through
 * ViewportEvent.ComputeFogColor, and render their own procedural sun/moon
 * position independently of vanilla's - our AFTER_SKY moon overlay ends up
 * floating in the wrong spot on top of it. RedMoonSkyRenderer checks
 * shaderPackActive() to skip that 3D tinting entirely when a shader is loaded,
 * falling back to a flat 2D screen overlay instead (see ScreenTintOverlay).
 */
final class IrisCompat {

    private static final Method IS_SHADER_PACK_IN_USE;
    private static final Object IRIS_API_INSTANCE;

    static {
        Method method = null;
        Object instance = null;
        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            instance = apiClass.getMethod("getInstance").invoke(null);
            method = apiClass.getMethod("isShaderPackInUse");
        } catch (Throwable ignored) {
            // Iris not installed, or its API changed shape - either way, behave as if no shader is active.
        }
        IS_SHADER_PACK_IN_USE = method;
        IRIS_API_INSTANCE = instance;
    }

    private IrisCompat() {
    }

    static boolean shaderPackActive() {
        if (IS_SHADER_PACK_IN_USE == null) {
            return false;
        }
        try {
            return (boolean) IS_SHADER_PACK_IN_USE.invoke(IRIS_API_INSTANCE);
        } catch (Throwable ignored) {
            return false;
        }
    }
}
