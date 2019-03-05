package com.github.blockchain.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import com.github.blockchain.AndroidApplication;
import com.github.blockchain.ApplicationLoader;
import com.github.blockchain.models.Apk;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.NoOpProcessor;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BlockChainManager {

    private Web3j web3;
    private AndroidApplication token = null;
    private static BlockChainManager Instance;
    private final String password = "medium";
    private String walletPath;
    private Credentials credentials;
    private File walletDir;
    private String contractAddress = "0x29aDD9aDd5118fda8e53F1751A1a8D3636B6b308";
    private SharedPreferences sharedPreferences;
    private String walletName;

    private BlockChainManager() {
        walletDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!walletDir.exists()) {
            walletDir.mkdir();
        }
        walletPath = walletDir.getAbsolutePath();
        sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences(
                "block_chain", Context.MODE_PRIVATE);
        walletName = sharedPreferences.getString("wallet_name", "UTC--2019-02-25T21-27-07.1Z--2629ccdc5f2fb1b2139d15d5254920c88858d3bd.json");
    }

    public static BlockChainManager getInstance() {
        BlockChainManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (BlockChainManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new BlockChainManager();
                }
            }
        }
        return localInstance;
    }

    public void connectToEthNetwork() {
        toastAsync("Connecting to Ethereum network...");
        // FIXME: Add your own API key here
        web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/4fd6ad2344524a2999a353a3133edb39"));
        try {
            Web3ClientVersion clientVersion = web3.web3ClientVersion().sendAsync().get();
            if (!clientVersion.hasError()) {
                toastAsync("Connected!");
            } else {
                toastAsync(clientVersion.getError().getMessage());
            }
            deployContract();
        } catch (Exception e) {
            toastAsync(e.getMessage());
        }
    }

    public void createWallet() {

        try {

            walletName = WalletUtils.generateLightNewWalletFile(password, walletDir);
            sharedPreferences.edit().putString("wallet_name", walletName).apply();
            toastAsync("Wallet generated");

        } catch (Exception e) {
            toastAsync(e.getMessage());
        }
    }

    public Credentials getWallet() {

        try {
            if (credentials != null)
                return credentials;
            File wallet = new File(walletDir, walletName);
            if (!wallet.exists()) {
                createWallet();
                wallet = new File(walletDir, walletName);
            }
            credentials = WalletUtils.loadCredentials(password, wallet);
            toastAsync("Wallet loaded");
            return credentials;
        } catch (Exception e) {
            toastAsync(e.getMessage());
            return null;
        }
    }

    public void getMyApplication(ApplicationDelegate applicationDelegate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                applicationDelegate.OnSuccess(new ArrayList<>());
            }
        }).start();
    }

    public ArrayList<Apk> getMyApplication() {
        ArrayList<Apk> apks = new ArrayList<>();

        try {
            for (int i = 0; ; i++) {
                Tuple5 object = token.ApplicationDatabase(getWallet().getAddress(), BigInteger.valueOf(i)).send();
                Apk apk = new Apk(object.getValue1().toString(), "", object.getValue2().toString(), "", false, object.getValue3().toString());
                apks.add(apk);
            }
            //apks.add((apkobject)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apks;
    }

    public boolean addApplications(String name, String packageName, String sing, String NPublickkey, String challeng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object object1 = token.addApplication(packageName, name, sing, NPublickkey, "test.com", challeng).send();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    public boolean getVerifiedApp(String name, String packageName, String sing, VerifiedDelegate verifiedDelegate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String newsing = sing.replaceAll("\n", "");
                    String object1 = token.getVerifiedApp(packageName, name, newsing).send();
                    verifiedDelegate.OnSuccess(object1);
                } catch (Exception e) {
                    e.printStackTrace();
                    verifiedDelegate.OnError();
                }
            }
        }).start();
        return true;
    }

    public boolean verifyApplication(String name, String packageName, String sing, int appId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object object1 = token.verifyApplication(packageName, name, sing, BigInteger.valueOf(appId)).send();

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();
        return true;
    }


    public void toastAsync(String message) {
        ApplicationLoader.applicationHandler.post(() -> {
            Toast.makeText(ApplicationLoader.applicationContext, message, Toast.LENGTH_LONG).show();
        });
    }


    interface ApplicationDelegate {
        void OnSuccess(List<Apk> apkList);

        void OnError();
    }

    public interface VerifiedDelegate {
        void OnSuccess(String packageName);

        void OnError();
    }

    public void deployContract() {

        try {
            Credentials credentials = getWallet();

            if (contractAddress != null) {
                // load existing contract by address
                token = AndroidApplication.load(contractAddress, web3, credentials, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);

                if (token.isValid())
                    toastAsync("AndroidApplication isValid");
                else
                    toastAsync("AndroidApplication is not valid");
                return;
            }
            NoOpProcessor processor = new NoOpProcessor(web3);
            TransactionManager txManager = new FastRawTransactionManager(web3, credentials, processor);

            RemoteCall<AndroidApplication> request = AndroidApplication.deploy(web3, credentials, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
            token = request.send();
            contractAddress = token.getDeployedAddress("3"); // 3 is ropsten testnet

        } catch (Exception e) {
            toastAsync(e.getMessage());
            e.printStackTrace();
        }

    }


}
