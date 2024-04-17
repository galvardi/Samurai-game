package pepse.world;

/**
 * this class is used to allow for the random generation of the terrain
 * implementation taken from GitHub
 */
public class NoiseGenerator {
    private static final int SIZE = 512;
    private static final int POPULATE = 256;
    private static final double DOUBLE_ZERO = 0.0;
    private static final double TWO_FLOAT = 2f;
    private double seed;
    private long default_size;
    private int[] p;
    private int[] permutation;


    private static final int DEFAULT_SIZE = 35;
        private static final int NUM_ONE = 1;
        private static final int NUM_TWO_HUNDRED_FIFTY_FIVE = 255;
        private static final int NUM_SIX = 6;
        private static final int NUM_FIFTEEN = 15;
        private static final int NUM_TEN = 10;
        private static final int NUM_EIGHT = 8;
        private static final int NUM_FOUR = 4;
        private static final int NUM_TWELVE = 12;
        private static final int NUM_FOURTEEN = 14;
        private static final int NUM_ZERO = 0;
        private static final int NUM_TWO = 2;
        private static final int[] PERMUTATION = {151, 160, 137, 91, 90, NUM_FIFTEEN, 131, 13, 201,
                95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, NUM_EIGHT, 99,
                37, 240, 21, NUM_TEN, 23, 190, NUM_SIX, 148, 247, 120, 234, 75, NUM_ZERO, 26,
                197, 62, 94, 252, 219, 203, 117, DEFAULT_SIZE, 11, 32, 57, 177, 33, 88,
                237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74,
                165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
                229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
                244, 102, 143, 54, 65, 25, 63, 161, NUM_ONE, 216, 80, 73, 209, 76,
                132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
                86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
                124, 123, 5, 202, 38, 147, 118, 126, NUM_TWO_HUNDRED_FIFTY_FIVE, 82, 85, 212, 207,
                206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
                213, 119, 248, 152, NUM_TWO, 44, 154, 163, 70, 221, 153, 101, 155,
                167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
                224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
                193, 238, 210, 144, NUM_TWELVE, 191, 179, 162, 241, 81, 51, 145, 235,
                249, NUM_FOURTEEN, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
                84, 204, 176, 115, 121, 50, 45, 127, NUM_FOUR, 150, 254, 138, 236,
                205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
                215, 61, 156, 180};

        public NoiseGenerator(double seed) {
            this.seed = seed;
            init();
        }

        private void init() {
            // Initialize the permutation array.
            this.p = new int[SIZE];
            this.permutation = PERMUTATION;
            this.default_size = DEFAULT_SIZE;

            // Populate it
            for (int i = NUM_ZERO; i < POPULATE; i++) {
                p[POPULATE + i] = p[i] = permutation[i];
            }

        }

        public double noise(double x) {
            double value = DOUBLE_ZERO;
            double size = default_size;
            double initialSize = size;

            while (size >= NUM_ONE) {
                value += smoothNoise((x / size), (0f / size), (0f / size)) * size;
                size /= TWO_FLOAT;
            }

            return value / initialSize;
        }

        public double smoothNoise(double x, double y, double z) {
            // Offset each coordinate by the seed value
            x += this.seed;
            y += this.seed;
            x += this.seed;

            int X = (int) Math.floor(x) & NUM_TWO_HUNDRED_FIFTY_FIVE; // FIND UNIT CUBE THAT
            int Y = (int) Math.floor(y) & NUM_TWO_HUNDRED_FIFTY_FIVE; // CONTAINS POINT.
            int Z = (int) Math.floor(z) & NUM_TWO_HUNDRED_FIFTY_FIVE;

            x -= Math.floor(x); // FIND RELATIVE X,Y,Z
            y -= Math.floor(y); // OF POINT IN CUBE.
            z -= Math.floor(z);

            double u = fade(x); // COMPUTE FADE CURVES
            double v = fade(y); // FOR EACH OF X,Y,Z.
            double w = fade(z);

            int A = p[X] + Y;
            int AA = p[A] + Z;
            int AB = p[A + NUM_ONE] + Z; // HASH COORDINATES OF
            int B = p[X + NUM_ONE] + Y;
            int BA = p[B] + Z;
            int BB = p[B + NUM_ONE] + Z; // THE 8 CUBE CORNERS,

            return lerp(w, lerp(v, lerp(u, grad(p[AA], 		x, 		y, 		z		)
                                    , 	// AND ADD
                                    grad(p[BA],		x - NUM_ONE, 	y, 		z		)), // BLENDED
                            lerp(u, grad(p[AB], 	x, 		y - NUM_ONE, 	z		), 	// RESULTS
                                    grad(p[BB], 	x - NUM_ONE, 	y - NUM_ONE, 	z		)))
                    ,// FROM 8
                    lerp(v, lerp(u, grad(p[AA + NUM_ONE], x, 		y, 		z - NUM_ONE), 	// CORNERS
                                    grad(p[BA + NUM_ONE], x - NUM_ONE, 	y, 		z - NUM_ONE)),
                            // OF CUBE
                            lerp(u, grad(p[AB + NUM_ONE], x, 		y - NUM_ONE,	z - NUM_ONE),
                                    grad(p[BB + NUM_ONE], x - NUM_ONE,
                                            y - NUM_ONE, 	z - NUM_ONE))));
        }

        private double fade(double t) {
            return t * t * t * (t * (t * NUM_SIX - NUM_FIFTEEN) + NUM_TEN);
        }

        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double y, double z) {
            int h = hash & NUM_FIFTEEN; // CONVERT LO 4 BITS OF HASH CODE
            double u = h < NUM_EIGHT ? x : y, // INTO 12 GRADIENT DIRECTIONS.
                    v = h < NUM_FOUR ? y : h == NUM_TWELVE || h == NUM_FOURTEEN ? x : z;
            return ((h & NUM_ONE) == NUM_ZERO ? u : -u) + ((h & NUM_TWO) == NUM_ZERO ? v : -v);
        }
}
