class Spring implements Updatable {
    private double pos = 0.0;
    private double vel;
    private double omega;
    private double alpha;
    private double dt = 1.0 / 60;

    public double getPos() { return pos; }

    public void setParameters(double f, double alpha, double vel) {
        this.alpha = alpha;
        this.omega = 2 * Math.PI * f;
        this.vel = vel;
    }

    public void update() {
        pos += vel * dt;
        vel -= omega*omega*pos*dt;
        vel = vel*(1-alpha);
    }

    public static void main(String[] args) {
        Spring s = new Spring();
        s.setParameters(5, 0.05, 1.0);
        for (int i=0; i<60*3; i++) {
            System.out.println(s.getPos());
            s.update();
        }
    }
}