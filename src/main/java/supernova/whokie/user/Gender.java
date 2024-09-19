package supernova.whokie.user;

public enum Gender {
    F, M;

    public static Gender fromString(String gender) {
        if (gender.equals("male")) {
            return M;
        }
        return F;
    }
}
