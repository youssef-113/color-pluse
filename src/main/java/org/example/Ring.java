package org.example;

import com.jogamp.opengl.GL2;

public class Ring {
    private float y;
    private float innerRadius, outerRadius;
    private float rotationSpeed;
    private float currentAngle = 0;
    private boolean passed = false;

    private static final float[][] COLORS = {
            {0.0f, 1.0f, 1.0f}, // Cyan
            {1.0f, 1.0f, 0.0f}, // Yellow
            {1.0f, 0.0f, 1.0f}, // Magenta
            {0.5f, 0.0f, 1.0f}  // Purple
    };

    public Ring(float y, float innerRadius, float outerRadius, float rotationSpeed) {
        this.y = y;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.rotationSpeed = rotationSpeed;
    }

    public void update() {
        currentAngle += rotationSpeed;
        if (currentAngle > 360) currentAngle -= 360;
        if (currentAngle < 0) currentAngle += 360;
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(0, y, 0);
        gl.glRotatef(currentAngle, 0, 0, 1);

        for (int i = 0; i < 4; i++) {
            gl.glColor3fv(COLORS[i], 0);
            drawArc(gl, 0, 0, innerRadius, outerRadius, i * 90, (i + 1) * 90, 16);
        }

        gl.glPopMatrix();
    }

    private void drawArc(GL2 gl, float cx, float cy, float r1, float r2, float startAngle, float endAngle, int numSegments) {
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        for (int i = 0; i <= numSegments; i++) {
            double angle = Math.toRadians(startAngle + (endAngle - startAngle) * i / numSegments);
            float x1 = cx + (float) (r1 * Math.cos(angle));
            float y1 = cy + (float) (r1 * Math.sin(angle));
            float x2 = cx + (float) (r2 * Math.cos(angle));
            float y2 = cy + (float) (r2 * Math.sin(angle));
            gl.glVertex2f(x1, y1);
            gl.glVertex2f(x2, y2);
        }
        gl.glEnd();
    }

    public int getSegmentAtAngle(float px, float py) {
        double angle = Math.toDegrees(Math.atan2(py - this.y, px - 0));
        if (angle < 0) {
            angle += 360;
        }

        // Adjust for ring's rotation
        angle = (angle - currentAngle + 360) % 360;

        if (angle >= 0 && angle < 90) return 0;       // First segment (e.g., Cyan)
        if (angle >= 90 && angle < 180) return 1;     // Second (e.g., Yellow)
        if (angle >= 180 && angle < 270) return 2;    // Third (e.g., Magenta)
        if (angle >= 270 && angle < 360) return 3;    // Fourth (e.g., Purple)

        return -1; // Should not happen
    }


    // Getters and Setters
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getInnerRadius() { return innerRadius; }
    public float getOuterRadius() { return outerRadius; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
}

