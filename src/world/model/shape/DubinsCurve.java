package world.model.shape;

import java.io.ObjectInputStream.GetField;
import java.util.LinkedList;
import java.util.List;
import util.DistanceUtil;

/**
 * This class implements logic for computing Dubins curves. Most of the code is
 * just Java reimplementation of ompl::base::DubinsStateSpace class from
 * Open-Motion Planning Library OMPL
 */
public class DubinsCurve {

    final static double TWOPI = 2 * Math.PI;
    final static double DUBINS_EPS = 1e-6;
    final static double DUBINS_ZERO = -1e-9;

    public static void main(String[] args)
    {
        Point start = new Point(100, 100, Math.PI / 2);
        Point end = new Point(-400, -300, Math.PI / 4);
        System.out.println(Math.toDegrees(0.2));
        DubinsCurve dc = new DubinsCurve(start, end, 1, false);

        final Trajectory traj = dc.getTrajectory(10, 1);
                System.out.println(end);
        System.out.println("distance:" + DistanceUtil.distanceBetween(new float[]{100,100}, new float[]{-400,-300}));
        System.out.println("total cost:"+traj.getCost());
                System.out.println("total time:"+traj.getMaxTime());
        System.out.println(traj.get(traj.getMaxTime()));
    }
    
    public static class DubinsPath {

        public static enum Segment {

            LEFT, STRAIGHT, RIGHT
        }
        /**
         * Path segment types
         */
        Segment[] type;
        /**
         * Path segment lengths
         */
        double[] lengths = new double[3];
        /**
         * Whether the path should be followed "in reverse"
         */
        boolean reverse = false;

        DubinsPath() {
            this(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT}, 0, Double.MAX_VALUE, 0);
        }

        DubinsPath(Segment[] type, double t, double p, double q) {
            assert type.length == 3;
            this.type = type;
            lengths[0] = t;
            lengths[1] = p;
            lengths[2] = q;
            assert (t >= 0.);
            assert (p >= 0.);
            assert (q >= 0.);
        }

        public double length() {
            return lengths[0] + lengths[1] + lengths[2];
        }

        public double[] getSegmentLengths() {
            return lengths;
        }

        public Segment[] getSegmentTypes() {
            return type;
        }
    };

    static double mod2pi(double angleInRads) {
        if (angleInRads < 0 && angleInRads > DUBINS_ZERO) {
            // Angle is close to zero
            return 0;
        }
        return angleInRads - (TWOPI * Math.floor(angleInRads / TWOPI));
    }

    private Point start;
    private double rho;
    private DubinsPath path;

    public DubinsCurve(Point start, Point end, double rho) {
        this(start, end, rho, false);
    }

    /** 
     *
     * @param start
     * @param end
     * @param rho
     * @param reverseAllowed
     */
    public DubinsCurve(Point start, Point end, double rho, boolean reverseAllowed) {
        this.start = start;
        this.rho = rho;

        this.path = getDubinsPath(start, end, rho);

        if (reverseAllowed) {
            DubinsPath reversedPath = getDubinsPath(end, start, rho);
            if (reversedPath.length() < path.length()) {
                reversedPath.reverse = true;
                this.path = reversedPath;
            }
        }
    }

    protected DubinsPath getDubinsPath(Point start, Point end, double rho) {
        // Normalize to relative coordinate system, where the origin is at start (0,0)
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double d = Math.sqrt(dx * dx + dy * dy) / rho; // normalize to r_min = 1
        double th = Math.atan2(dy, dx);
        double alpha = mod2pi(start.getYaw() - th);
        double beta = mod2pi(end.getYaw() - th);

        return canonicalDubins(d, alpha, beta);
    }

    public Trajectory getTrajectory(double speed, int samplingInterval) {

        double duration = (path.length() * rho) / speed;
        int nPoints = (int) Math.floor(duration / samplingInterval);

        Point points[] = new Point[nPoints];

        for (int i = 0; i < nPoints; i++) {
            points[i] = interpolate((i * samplingInterval) / duration);
        }

        Trajectory traj = new Trajectory(points, samplingInterval, path.length() * rho);
        return traj;
    }

    // Canonical Dubins. Start is assumed to be at (0,0) with orientation 0 rad. Minimum turn radius is assumed to be 1. Are lengths of all segments are in multiplies of rho.
    static private DubinsPath canonicalDubins(double d, double alpha, double beta) {
        if (d < DUBINS_EPS && Math.abs(alpha - beta) < DUBINS_EPS) {
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT}, 0, d, 0);
        }

        DubinsPath bestPath = dubinsLSL(d, alpha, beta);
        double minLength = bestPath.length();

        DubinsPath tmp = dubinsRSR(d, alpha, beta);
        double len;

        if ((len = tmp.length()) < minLength) {
            minLength = len;
            bestPath = tmp;
        }

        tmp = dubinsRSL(d, alpha, beta);
        if ((len = tmp.length()) < minLength) {
            minLength = len;
            bestPath = tmp;
        }
        tmp = dubinsLSR(d, alpha, beta);
        if ((len = tmp.length()) < minLength) {
            minLength = len;
            bestPath = tmp;
        }
        tmp = dubinsRLR(d, alpha, beta);
        if ((len = tmp.length()) < minLength) {
            minLength = len;
            bestPath = tmp;
        }
        tmp = dubinsLRL(d, alpha, beta);
        if ((len = tmp.length()) < minLength) {
            bestPath = tmp;
        }

        return bestPath;
    }

    static DubinsPath dubinsLSL(double d, double alpha, double beta) {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = 2. + d * d - 2. * (ca * cb + sa * sb - d * (sa - sb));
        if (tmp >= DUBINS_ZERO) {
            double theta = Math.atan2(cb - ca, d + sa - sb);
            double t = mod2pi(-alpha + theta);
            double p = Math.sqrt(Math.max(tmp, 0.));
            double q = mod2pi(beta - theta);
            assert (Math.abs(p * Math.cos(alpha + t) - sa + sb - d) < DUBINS_EPS);
            assert (Math.abs(p * Math.sin(alpha + t) + ca - cb) < DUBINS_EPS);
            assert (mod2pi(alpha + t + q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT}, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsRSR(double d, double alpha, double beta) {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = 2. + d * d - 2. * (ca * cb + sa * sb - d * (sb - sa));
        if (tmp >= DUBINS_ZERO) {
            double theta = Math.atan2(ca - cb, d - sa + sb);
            double t = mod2pi(alpha - theta);
            double p = Math.sqrt(Math.max(tmp, 0.));
            double q = mod2pi(-beta + theta);
            assert (Math.abs(p * Math.cos(alpha - t) + sa - sb - d) < DUBINS_EPS);
            assert (Math.abs(p * Math.sin(alpha - t) - ca + cb) < DUBINS_EPS);
            assert (mod2pi(alpha - t - q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.RIGHT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.RIGHT}, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsRSL(double d, double alpha, double beta) {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = d * d - 2. + 2. * (ca * cb + sa * sb - d * (sa + sb));
        if (tmp >= DUBINS_ZERO) {
            double p = Math.sqrt(Math.max(tmp, 0.));
            double theta = Math.atan2(ca + cb, d - sa - sb) - Math.atan2(2., p);
            double t = mod2pi(alpha - theta);
            double q = mod2pi(beta - theta);
            assert (Math.abs(p * Math.cos(alpha - t) - 2. * Math.sin(alpha - t) + sa + sb - d) < DUBINS_EPS);
            assert (Math.abs(p * Math.sin(alpha - t) + 2. * Math.cos(alpha - t) - ca - cb) < DUBINS_EPS);
            assert (mod2pi(alpha - t + q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.RIGHT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT}, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsLSR(double d, double alpha, double beta) {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = -2. + d * d + 2. * (ca * cb + sa * sb + d * (sa + sb));
        if (tmp >= DUBINS_ZERO) {
            double p = Math.sqrt(Math.max(tmp, 0.));
            double theta = Math.atan2(-ca - cb, d + sa + sb) - Math.atan2(-2., p);
            double t = mod2pi(-alpha + theta);
            double q = mod2pi(-beta + theta);
            assert (Math.abs(p * Math.cos(alpha + t) + 2. * Math.sin(alpha + t) - sa - sb - d) < DUBINS_EPS);
            assert (Math.abs(p * Math.sin(alpha + t) - 2. * Math.cos(alpha + t) + ca + cb) < DUBINS_EPS);
            assert (mod2pi(alpha + t - q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.RIGHT}, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsRLR(double d, double alpha, double beta) {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = .125 * (6. - d * d + 2. * (ca * cb + sa * sb + d * (sa - sb)));
        if (Math.abs(tmp) < 1.) {
            double p = TWOPI - Math.acos(tmp);
            double theta = Math.atan2(ca - cb, d - sa + sb);
            double t = mod2pi(alpha - theta + .5 * p);
            double q = mod2pi(alpha - beta - t + p);
            assert (Math.abs(2. * Math.sin(alpha - t + p) - 2. * Math.sin(alpha - t) - d + sa - sb) < DUBINS_EPS);
            assert (Math.abs(-2. * Math.cos(alpha - t + p) + 2. * Math.cos(alpha - t) - ca + cb) < DUBINS_EPS);
            assert (mod2pi(alpha - t + p - q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.RIGHT, DubinsPath.Segment.LEFT, DubinsPath.Segment.RIGHT}, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsLRL(double d, double alpha, double beta) {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = .125 * (6. - d * d + 2. * (ca * cb + sa * sb - d * (sa - sb)));
        if (Math.abs(tmp) < 1.) {
            double p = TWOPI - Math.acos(tmp);
            double theta = Math.atan2(-ca + cb, d + sa - sb);
            double t = mod2pi(-alpha + theta + .5 * p);
            double q = mod2pi(beta - alpha - t + p);
            assert (Math.abs(-2. * Math.sin(alpha + t - p) + 2. * Math.sin(alpha + t) - d - sa + sb) < DUBINS_EPS);
            assert (Math.abs(2. * Math.cos(alpha + t - p) - 2. * Math.cos(alpha + t) + ca - cb) < DUBINS_EPS);
            assert (mod2pi(alpha + t - p + q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.RIGHT, DubinsPath.Segment.LEFT}, t, p, q);
        }
        return new DubinsPath();
    }

    public Point[] interpolateAdaptiveBy(double samplingInterval) {

        List<Point> points = new LinkedList<Point>();

        if (!path.reverse) {
            for (int i = 0; i < 3; i++) {
                points.addAll(interpolateSegment(i, samplingInterval, false));
            }
        } else {
            for (int i = 2; i >= 0; i--) {
                points.addAll(interpolateSegment(i, samplingInterval, true));
            }
        }
        points.add(interpolate(1));

        return points.toArray(new Point[points.size()]);
    }

    protected List<Point> interpolateSegment(int i, double samplingInterval, boolean isReverse) {
        double len = path.length();
        List<Point> points = new LinkedList<Point>();
        if (path.type[i] == DubinsCurve.DubinsPath.Segment.LEFT || path.type[i] == DubinsCurve.DubinsPath.Segment.RIGHT) {
            // C - segment
            double startLen = 0;
            double endLen = 0;

            double samplingStep = samplingInterval / getLength();
            if (!isReverse()) {
                // foward

                if (i == 0) {
                    startLen = 0;
                    endLen = path.lengths[0];
                } else if (i == 1) {
                    startLen = path.lengths[0];
                    endLen = startLen + path.lengths[1];
                } else if (i == 2) {
                    startLen = path.lengths[0] + path.lengths[1];
                    endLen = startLen + path.lengths[2];
                } else {
                    assert false;
                }
            } else {
                // reverse

                if (i == 2) {
                    startLen = 0;
                    endLen = path.lengths[2];
                } else if (i == 1) {
                    startLen = path.lengths[2];
                    endLen = startLen + path.lengths[1];
                } else if (i == 0) {
                    startLen = path.lengths[2] + path.lengths[1];
                    endLen = startLen + path.lengths[0];
                } else {
                    assert false;
                }
            }

            // interpolate on interval [startLen,endLen)
            for (double alpha = startLen / len; alpha < endLen / len; alpha += samplingStep) {
                points.add(interpolate(alpha));
            }

        } else {
            // S - segment
            assert i == 1;
            if (!isReverse()) {
                // forward
                points.add(interpolate(path.lengths[0] / len));
            } else {
                // reverse
                points.add(interpolate(path.lengths[2] / len));
            }
        }

        return points;
    }

    public Point[] interpolateUniformBy(double samplingInterval) {

        int nPoint = (int) Math.ceil(getLength() / samplingInterval);
        Point[] res = new Point[nPoint];

        for (int i = 0; i < res.length; i++) {
            res[i] = interpolate(i / (double) res.length);
        }

        return res;
    }

    /**
     * Interpolates position on the path for t from [0,1] *
     */
    public Point interpolate(double t) {
        double seg = t * path.length();
        double phi = start.getYaw();
        double v;

        Point s = new Point(0, 0, start.getYaw());

        if (!path.reverse) {
            for (int i = 0; i < 3 && seg > 0; i++) {
                v = Math.min(seg, path.lengths[i]);
                phi = s.getYaw();
                seg -= v;

                switch (path.type[i]) {
                    case LEFT:
                        s.x = s.x + Math.sin(phi + v) - Math.sin(phi);
                        s.y = s.y - Math.cos(phi + v) + Math.cos(phi);
                        s.z = phi + v;
                        break;

                    case RIGHT:
                        s.x = s.x - Math.sin(phi - v) + Math.sin(phi);
                        s.y = s.y + Math.cos(phi - v) - Math.cos(phi);
                        s.z = phi - v;
                        break;

                    case STRAIGHT:
                        s.x = s.x + v * Math.cos(phi);
                        s.y = s.y + v * Math.sin(phi);
                        break;
                }
            }
        } else {
            for (int i = 0; i < 3 && seg > 0; i++) {
                v = Math.min(seg, path.lengths[2 - i]);
                phi = s.getYaw();
                seg -= v;

                switch (path.type[2 - i]) {
                    case LEFT:
                        s.x = s.x + Math.sin(phi - v) - Math.sin(phi);
                        s.y = s.y - Math.cos(phi - v) + Math.cos(phi);
                        s.z = phi - v;
                        break;
                    case RIGHT:
                        s.x = s.x - Math.sin(phi + v) + Math.sin(phi);
                        s.y = s.y + Math.cos(phi + v) - Math.cos(phi);
                        s.z = phi + v;
                        break;
                    case STRAIGHT:
                        s.x = s.x - v * Math.cos(phi);
                        s.y = s.y - v * Math.sin(phi);
                        break;
                }
            }
        }

        Point res = new Point(s.x * rho + start.x, s.y * rho + start.y, s.getYaw());
        return res;
    }

    public double getLength() {
        return path.length() * rho;
    }

    public boolean isReverse() {
        return path.reverse;
    }

    public DubinsPath getCanonicalPath() {
        return path;
    }

}
