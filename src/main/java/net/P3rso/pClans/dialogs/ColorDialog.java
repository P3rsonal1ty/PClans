package net.P3rso.pClans.dialogs;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ColorDialog {
    private static DialogAction action = DialogAction.customClick(
            (view, audience)->{
                int r = view.getFloat("R").intValue();
                int g = view.getFloat("G").intValue();
                int b = view.getFloat("B").intValue();
                if(audience instanceof  Player player) openColorDialog(player,r,g,b);
            }, ClickCallback.Options.builder()
                    .uses(100) // Set the number of uses for this callback. Defaults to 1
                    .lifetime(ClickCallback.DEFAULT_LIFETIME) // Set the lifetime of the callback. Defaults to 12 hours
                    .build()
    );

    private static ActionButton buttonCheck = ActionButton.create(
            Component.text("Проверить цвет", TextColor.color(0xFFA0B1)),
            Component.text("Нажмите чтобы посмотреть результат!"),
            100,
            action

    );
    private static ActionButton buttonAccept = ActionButton.create(
            Component.text("Подтвердить", TextColor.color(0xAEFFC1)),
            Component.text("Нажмите чтобы подтвердить новый цвет"),
            100,
        action
    );

    public static void openColorDialog(Player player, int R, int G, int B){
        int hex_color = (R << 16) | (G << 8) | B;
        Component name = Component.text("Выбор цвета клана!").color(TextColor.color(hex_color));
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(name)
                        .inputs(List.of(
                                DialogInput.numberRange("R",Component.text("R", NamedTextColor.RED),0f,255f)
                                        .step(1f)
                                        .initial((float) R)
                                        .width(255)
                                        .build(),
                                DialogInput.numberRange("G",Component.text("G", NamedTextColor.GREEN),0f,255f)
                                        .step(1f)
                                        .initial((float) G)
                                        .width(255)
                                        .build(),
                                DialogInput.numberRange("B",Component.text("B", NamedTextColor.BLUE),0f,255f)
                                        .step(1f)
                                        .initial((float) B)
                                        .width(255)
                                        .build()
                        ))
                        .afterAction(DialogBase.DialogAfterAction.NONE)
                        .canCloseWithEscape(true)
                        .pause(false)
                        .build()
                )
                .type(DialogType.multiAction(List.of(buttonCheck,buttonAccept)).build()
                )
        );
        player.showDialog(dialog);
    }



}
