package russianCorpusAPI.config;

public class CommonConstants {

    private static volatile CommonConstants instance;
    private static boolean flag = true;

    private CommonConstants() {
        // to prevent instantiating by Reflection call
        if (flag) {
            flag = false;
        } else {
            throw new IllegalStateException("CommonConstants already initialized.");
        }
    }

    /**
     * Public accessor.
     *
     * @return an instance of the class.
     */
    public static CommonConstants getInstance() {
        // local variable increases performance by 25 percent
        // Joshua Bloch "Effective Java, Second Edition", p. 283-284

        //var result = instance;
        //in Java 8
        CommonConstants result = instance;

        // Check if singleton instance is initialized.
        // If it is initialized then we can return the instance.
        if (result == null) {
            // It is not initialized but we cannot be sure because some other thread might have
            // initialized it in the meanwhile.
            // So to make sure we need to lock on an object to get mutual exclusion.
            synchronized (CommonConstants.class) {
                // Again assign the instance to local variable to check if it was initialized by some
                // other thread while current thread was blocked to enter the locked zone.
                // If it was initialized then we can return the previously created instance
                // just like the previous null check.
                result = instance;
                if (result == null) {
                    // The instance is still not initialized so we can safely
                    // (no other thread can enter this zone)
                    // create an instance and make it our singleton instance.
                    instance = result = new CommonConstants();
                }
            }
        }
        return result;
    }
}
