package be.isach.ultracosmetics.economy;

import be.isach.ultracosmetics.UltraCosmetics;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.currency.CurrencyManager;

public class CoinsEngineHook implements EconomyHook {
    private final Currency currency;

    public CoinsEngineHook(UltraCosmetics ultraCosmetics, String currencyName) {
        if (currencyName == null) {
            throw new IllegalArgumentException("No CoinsEngine currency defined in config!");
        }
        this.currency = CoinsEngineAPI.getCurrency(currencyName);
        if (currency == null) {
            throw new IllegalArgumentException("Couldn't find specified CoinsEngine currency '" + currencyName + "'");
        }
    }

    @Override
    public void withdraw(Player player, int amount, Runnable onSuccess, Runnable onFailure) {
        double balance = CoinsEngineAPI.getBalance(player, currency);
        if (balance < amount) {
            onFailure.run();
            return;
        }
        CoinsEngineAPI.removeBalance(player, currency, amount);
        onSuccess.run();
    }

    @Override
    public void deposit(Player player, int amount) {
        CoinsEngineAPI.addBalance(player, currency, amount);
    }

    @Override
    public String getName() {
        return "CoinsEngine:" + currency.getName();
    }
}
