package JinManager;

import com.jinhuynh.jdbc.daos.PlayerDAO;
import com.jinhuynh.server.Client;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author Jin
 */
public class JinManager {
    private static JinManager instance = null;
    
    private JinManager() {
        compositeDisposable = new CompositeDisposable();
    }
    public static synchronized JinManager getInstance() {
        if (instance == null) {
            instance = new JinManager();
        }
        return instance;
    }
    
    private CompositeDisposable compositeDisposable;
    
    public void autoSave() {
        System.out.println("[AutoSaveManager] start autosave");
        Disposable subscribe = Observable.interval(60, 90, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe(i -> {
                    this.handleAutoSave();
                },  throwable -> {
              System.out.println("[AutoSaveManager] start autosave error: " + throwable.getLocalizedMessage());
        });
        compositeDisposable.add(subscribe);               
    }
    
    private void handleAutoSave() {
        Client.gI().getPlayers().forEach(player -> {
            System.out.println("Save data success of player:" + player.name);
            PlayerDAO.updatePlayer(player);
        });
    }
    
    private void dispose() {
        compositeDisposable.dispose();
        compositeDisposable = null;
    }
}
