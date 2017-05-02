package com.untitled.mobiledocumentscanner;

public class CryptTest {
/*
    private String password = "test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypt_test);

        String test = "blah blah";

        byte[] imageData = new byte[0];
        SecureRandom random = new SecureRandom();
        byte[] encryptionKey = random.generateSeed(16);
        byte[] encryptedKey = new byte[0];
        try {
            Log.d("TEST", "start");
            // Encrypt image with key
            Key key = new SecretKeySpec(encryptionKey, "AES/ECB/PKCS5Padding");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            imageData = cipher.doFinal(test.getBytes());

            Log.d("TEST", "encrypt key");
            // Encrypt key with password
            // Generate key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            random.setSeed(password.getBytes());
            keyGen.init(128, random);
            key = keyGen.generateKey();
            // Do encryption
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedKey = cipher.doFinal(encryptionKey);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.d("TEST", "Crypt fail");
        }

        Log.d("TEST", Base64.encodeToString(imageData, Base64.DEFAULT));
        Log.d("TEST", Base64.encodeToString(encryptedKey, Base64.DEFAULT));


        String encryptedKey = "hAqgDrAtjoOS/wNfOK4Vb0XQJqm8/oLp2DVPI4pAK4c=";
        String text = "f4Bf/GRutTwei4vHgi4hPQ==";
        byte[] bytes = new byte[0];
        try {
            // Decrypt key
            SecureRandom random = new SecureRandom();
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            random.setSeed(password.getBytes());
            keyGen.init(128, random);
            Key key = keyGen.generateKey();

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptionKey = cipher.doFinal(Base64.decode(encryptedKey, Base64.DEFAULT));

            // Second pass with key
            key = new SecretKeySpec(encryptionKey, "AES/ECB/PKCS5Padding");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            bytes = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        String out = new String(bytes);
        Log.d("TEST", out);
    }
*/
}
