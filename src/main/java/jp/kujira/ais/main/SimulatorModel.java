package jp.kujira.ais.main;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jp.kujira.ais.provider.Provider;
import jp.kujira.ais.provider.Providers;
import jp.kujira.ais.server.AISServer;
import jp.kujira.ais.server.AgricultureInformationAgent;
import jp.kujira.ais.user.User;
import jp.kujira.ais.user.Users;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static jp.kujira.ais.main.Constants.INITIAL_PROPERTIES;

/**
 * Created by Kujira on 2017/04/03.
 */
public final class SimulatorModel {
    // シングルトンクラスのインスタンス類
    private static final SimulatorModel INSTANCE = new SimulatorModel();
    private SimulatorModel(){
        aisProperties = FXCollections.observableArrayList(INITIAL_PROPERTIES.entrySet().stream()
                .map(e -> new AISProperty(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }
    public static SimulatorModel getInstance(){
        return INSTANCE;
    }

    // ログ
    private StringBuilder logger = new StringBuilder("");
    private StringProperty loggerProperty = new SimpleStringProperty();

    // 各種設定パラメータのプロパティ
    private ObservableList<AISProperty> aisProperties;
    private Function<String, String> getProperty = key -> aisProperties.stream()
            .filter(p -> p.propertyNameProperty().getValue().equals(key))
            .findFirst()
            .orElseThrow(NoSuchElementException::new)
            .valueProperty().getValue();

    // プロバイダ及びユーザのList
    private ObservableList<Provider> providers = FXCollections.observableArrayList();
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<User> diligentUsers = FXCollections.observableArrayList();

    // 交互実行用のScheduledExecutorService
    private ScheduledExecutorService scheduledExecutorService;
    private AtomicInteger pc = new AtomicInteger(0);
    private AtomicInteger uc = new AtomicInteger(0);
    private AtomicInteger dc = new AtomicInteger(0);

    public void initialize(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            final String nop = getProperty.apply("numberOfProviders");        // プロバイダ(篤農家)の人数
            final String nou = getProperty.apply("numberOfUsers");            // ユーザ(新規就農者)の人数
            final String nod = getProperty.apply("numberOfDiligentUsers");            // ユーザ(篤農家)の人数
            final String minRI = getProperty.apply("minRequiredIncentive");   // 最小要求インセンティブ
            final String maxRI = getProperty.apply("maxRequiredIncentive");   // 最大要求インセンティブ
            final String nok = getProperty.apply("numberOfKnowHow");          // 所持ノウハウ数

            // プロバイダ(篤農家)の生成
            List<Provider> providers = IntStream.rangeClosed(1, Integer.valueOf(nop))
                    .mapToObj(i -> Providers.newRandomProvider(i, Integer.valueOf(minRI), Integer.valueOf(maxRI)))
                    .collect(Collectors.toList());
            this.providers.setAll(providers);

            // ユーザ(新規就農者)の生成
            List<User> users = IntStream.rangeClosed(1, Integer.valueOf(nou))
                    .mapToObj(i -> Users.newRandomSimpleUser())
                    .collect(Collectors.toList());
            this.users.setAll(users);

            // ユーザ(篤農家)の生成
            List<User> diligentUsers = IntStream.rangeClosed(1, Integer.valueOf(nod))
                    .mapToObj(i -> Users.newRandomDiligentUser())
                    .collect(Collectors.toList());
            this.diligentUsers.setAll(diligentUsers);

            // ノウハウの設定
            this.providers.stream().forEach(p -> {
                // 篤農家がノウハウを生成
                p.makeKnowHow(Integer.valueOf(nok));

                // 篤農家が自身のノウハウを評価
                p.evaluateKnowHow();
            });

            // ノウハウの削除
            AgricultureInformationAgent aia = (AgricultureInformationAgent) AISServer.getInstance().getAgricultureInformationAgent("ノウハウ");
            aia.clear();

            // ノウハウの提供
            this.providers.stream().forEach(Provider::provide);

            println(String.format("Initialized.(%s providers, %s users, %s diligent users, RI:[%s, %s])", nop, nou, nod, minRI, maxRI));
        });
        service.shutdown();
    }

    public void start(){
        if(Objects.isNull(scheduledExecutorService) || scheduledExecutorService.isShutdown()) {
            final int dot = Integer.valueOf(getProperty.apply("delayOfTurn")) * 1000;

            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            println(String.format("(re)Started."));

            // --- プロバイダ(篤農家)の行動 ---
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                providers.stream().forEach(p -> {
                    if(p.canRequire()){
                        p.requireIncentive();   // インセンティブ要求可能時はインセンティブを要求
                    }else{
                        p.decide();             // インセンティブ要求不可時(インセンティブ受取後)はノウハウを提供(一定確率)
                    }
                });
                final int c = pc.incrementAndGet();
                println(String.format("Providers' Turn %d ", c));
            }, 0, dot, TimeUnit.MILLISECONDS);

            // --- ユーザ(新規就農者)の行動 ---
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                users.stream().forEach(User::requireKnowHow);
                final int c = uc.incrementAndGet();
                println(String.format("Users' Turn %d ", c));
            }, dot/3, dot, TimeUnit.MILLISECONDS);

            // --- ユーザ(篤農家)の行動 ---
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                diligentUsers.stream().forEach(User::requireKnowHow);
                final int c = dc.incrementAndGet();
                println(String.format("Diligent Users' Turn %d ", c));
            }, (dot*2)/3, dot, TimeUnit.MILLISECONDS);
        }
    }

    public void stop(){
        if(Objects.nonNull(scheduledExecutorService) && !scheduledExecutorService.isShutdown()){
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleAtFixedRate(() -> {
                // プロバイダとユーザの行動回数が等しくなったときにスレッド停止
                if(pc.get() == uc.get() && uc.get() == dc.get() && pc.get() == dc.get()){
                    scheduledExecutorService.shutdown();
                    ses.shutdown();
                    println("Stopped.");
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    // ログ出力用
    private void print(final String x){
        logger.append(x);
        Platform.runLater(() -> loggerProperty.setValue(logger.toString()));
    }
    private void println(final String x){
        print(x + "\n");
    }

    public StringProperty getLoggerProperty(){
        return loggerProperty;
    }
    public ObservableList<AISProperty> getAISProperties(){ return aisProperties; }
    public ObservableList<Provider> getProviders(){ return providers; }
    public String getProperty(final String propertyName){
        return getProperty.apply(propertyName);
    }
}
