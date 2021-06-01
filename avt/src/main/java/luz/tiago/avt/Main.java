package luz.tiago.avt;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Date;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 *
 * @author tiagoluz
 */
public class Main {

    public final static int WIDTH = 1200;
    public final static int HEIGHT = 800;

    public final static long start = new Date().getTime();

    // The window handle
    private long window;

    public static void main(String[] args) {
        System.out.println("PUCRS - Métodos numéricos - Tiago Luz - 2021/01");
        Main main = new Main();
        main.run();
    }

    public Main() {

    }

    public void run() {

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    private void init() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Tiago Luz - Métodos Numéricos - AV T", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                System.exit(0);
            }
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

    }

    long age = 0;

    private void display() {

        glMatrixMode(GL_MODELVIEW);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glLoadIdentity();

        age = new Date().getTime() - start;

        drawBall1();

        drawBall2();

        // fim desenhar
        glFlush();
    }

    // linear 
    private void drawBall1() {
        glPushMatrix();
        double x = 300;
        double y = 700;

        double t = (age / 1000.0) % 7.0 * 2;

        if (t > 7) {
            y = 100 * t - 700;
        } else {
            y = -100 * t + 700;
        }

        glTranslated(x, y + 50, 0);
        glLineWidth(3);
        glColor4f(1f, 1f, 1f, 1f);
        drawBall();
        glPopMatrix();
    }

    // beziér
    private void drawBall2() {
        glPushMatrix();
        double x = 800;
        double y = 700;

        double b0 = 700;
        double b1 = 700;
        double b2 = 0;

        double tempo = (age / 1000.0) % 7.0 * 2;

        double t = 1.0 / 7.0 * (tempo - (tempo > 7 ? 7 : 0));

        if (tempo <= 7) {
            b0 = 700;
            b2 = 100;
        } else {
            b0 = 0;
            b2 = 700;
        }

        y = (Math.pow(1 - t, 2) * b0) + (2 * t * (1 - t) * b1) + Math.pow(t, 2) * b2;

        glTranslated(x, y + 50, 0);
        glLineWidth(3);
        glColor4f(1f, 1f, 1f, 1f);
        drawBall();
        glPopMatrix();
    }

    private void drawBall() {
        float radius = 50;
        float DEG2RAD = 3.14159f / 180.0f;

        glBegin(GL_POLYGON);

        for (int i = 0; i < 360; i++) {
            float degInRad = (float) i * DEG2RAD;
            glVertex2d(Math.cos(degInRad) * radius, Math.sin(degInRad) * radius);
        }

        glEnd();

    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glMatrixMode(GL11.GL_PROJECTION);
        glOrtho(0, WIDTH, 0, HEIGHT, 0, 1);
        glMatrixMode(GL11.GL_MODELVIEW);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            display();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

        }
    }

}
