package xyz.yusufyaser.aaafk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import xyz.yusufyaser.aaafk.UI.NumberFieldWidget;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class SettingsScreen extends Screen {
    private ServerInfo server = null;

    private ButtonWidget closeButton;

    private CheckboxWidget enabled;
    private CheckboxWidget hideMessagesEnabled;
    private ButtonWidget addMessage;

    private NumberFieldWidget afkTimerMinutes;
    private NumberFieldWidget afkTimerSeconds;
    private final ArrayList<TextFieldWidget> messageWidgets = new ArrayList<>();
    private final ArrayList<ButtonWidget> deleteMessageWidgets = new ArrayList<>();

    private boolean dataLoaded = false;
    ServerConfig config;

    protected SettingsScreen() {
        super(Text.literal("Anti Anti AFK - Server Settings"));
    }

    private void loadData() {
        if (dataLoaded) return;

        config = ServerConfig.getInstance(server.address);

        dataLoaded = true;
    }

    private void drawMessageFields() {
        for (TextFieldWidget messageWidget : messageWidgets) {
            remove(messageWidget);
        }
        messageWidgets.clear();
        for (ButtonWidget deleteMessageWidget : deleteMessageWidgets) {
            remove(deleteMessageWidget);
        }
        deleteMessageWidgets.clear();

        for (int i = 0; i < config.messages.toArray().length; i++) {
            String message = config.messages.get(i);
            TextFieldWidget chatMessage = new TextFieldWidget(
                    textRenderer,
                    274,
                    20,
                    Text.of(message)
            );
            chatMessage.setPosition(width / 2 - chatMessage.getWidth() / 2 - 13, 105 + ((i + 1) * 25));
            chatMessage.setMaxLength(255);
            chatMessage.setText(message);

            int finalI = i;
            ButtonWidget delete = ButtonWidget.builder(Text.literal("-"), button -> {
                        config.messages.remove(finalI);
                        drawMessageFields();
                    })
                    .dimensions(width / 2 + chatMessage.getWidth() / 2 - 10, 105 + ((i + 1) * 25), 20, 20)
                    .build();

            addDrawableChild(chatMessage);
            addDrawableChild(delete);
            messageWidgets.add(chatMessage);
            deleteMessageWidgets.add(delete);
        }
        remove(closeButton);
        addDrawableChild(closeButton);
    }

    @Override
    protected void init() {
        MinecraftClient client = MinecraftClient.getInstance();
        server = client.getCurrentServerEntry();

        loadData();

        closeButton = ButtonWidget.builder(Text.literal("Save & Close"), button -> {
                    this.close();
                })
                .dimensions(width / 2 - 100, height - 40, 200, 20)
                .build();

        enabled = CheckboxWidget.builder(Text.literal("Anti Anti AFK Enabled?"), textRenderer)
                .checked(config.enabled)
                .build();
        enabled.setPosition(width / 2 - enabled.getWidth() / 2, 40);

        afkTimerMinutes = new NumberFieldWidget(
                textRenderer,
                50,
                20,
                Text.literal("")
        );
        MutableText afkTimerTooltipHelp = Text.literal("Set this to the maximum time the server allows you to be AFK for to bypass Anti AFK automatically\n\n");
        MutableText afkTimerTooltipDisable = Text.literal("Set both values to 0 to disable").withColor(0xaaaaaa);
        Text afkTimerTooltip = afkTimerTooltipHelp.append(afkTimerTooltipDisable);
        afkTimerMinutes.setTooltip(Tooltip.of(afkTimerTooltip));
        afkTimerMinutes.setMax(30);
        afkTimerMinutes.setPosition(
                width / 2 - 50,
                80
        );
        afkTimerMinutes.setText(String.valueOf(config.afkTimer / 60));

        afkTimerSeconds = new NumberFieldWidget(
                textRenderer,
                50,
                20,
                Text.literal("")
        );
        afkTimerSeconds.setTooltip(afkTimerMinutes.getTooltip());
        afkTimerSeconds.setMax(59);
        afkTimerSeconds.setPosition(
                width / 2 + 50,
                80
        );
        afkTimerSeconds.setText(String.valueOf(config.afkTimer % 60));

        MutableText hideMessagesTooltipRecommended = Text.literal("Recommended").withColor(0xffff00);
        MutableText hideMessagesTooltipHelp = Text.literal("\nHide any AFK messages you get to avoid cluttering").withColor(0xffffff);
        Text hideMessagesTooltip = hideMessagesTooltipRecommended.append(hideMessagesTooltipHelp);

        hideMessagesEnabled = CheckboxWidget.builder(Text.literal("Hide AFK Message"), textRenderer)
                .pos(width / 2 - 150, 105)
                .checked(config.hideMessages)
                .tooltip(Tooltip.of(hideMessagesTooltip))
                .build();
        hideMessagesEnabled.setPosition(width / 2 - hideMessagesEnabled.getWidth() / 2, 105);

        addMessage = ButtonWidget.builder(Text.literal("+"), button -> {
                    config.messages.add("");
                    drawMessageFields();
                })
                .dimensions(width / 2 + 127, 105, 20, 20)
                .build();

        addMessage.active = config.enabled;

        drawMessageFields();

        addDrawableChild(enabled);

        addDrawableChild(afkTimerMinutes);
        addDrawableChild(afkTimerSeconds);

        addDrawableChild(hideMessagesEnabled);
        addDrawableChild(addMessage);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        config.enabled = enabled.isChecked();
        config.hideMessages = hideMessagesEnabled.isChecked();
        config.afkTimer = 0;
        if (!afkTimerSeconds.getText().isEmpty()) {
            config.afkTimer = Integer.parseInt(afkTimerSeconds.getText());
        }
        if (!afkTimerMinutes.getText().isEmpty()) {
            config.afkTimer += Integer.parseInt(afkTimerMinutes.getText()) * 60;
        }

        hideMessagesEnabled.active = config.enabled;
        afkTimerMinutes.active = config.enabled;
        afkTimerSeconds.active = config.enabled;

        addMessage.active = config.enabled && config.messages.size() < 5;

        for (int i = 0; i < messageWidgets.size(); i++) {
            TextFieldWidget messageWidget = messageWidgets.get(i);
            messageWidget.active = config.enabled;
            config.messages.set(i, messageWidget.getText());
        }
        for (ButtonWidget deleteMessageWidget : deleteMessageWidgets) {
            deleteMessageWidget.active = config.enabled;
        }

        context.drawCenteredTextWithShadow(textRenderer, Text.literal(server.name + " (" + server.address + ")"), width / 2, 20, 0xffffff);

        context.drawTextWithShadow(textRenderer, Text.literal("AFK Timer"), width / 2 - 150, 85, 0xffffff);
        context.drawTextWithShadow(textRenderer, Text.literal("minutes"), width / 2 + 3, 85, 0xffffff);
        context.drawTextWithShadow(textRenderer, Text.literal("seconds"), width / 2 + 103, 85, 0xffffff);

        context.drawTextWithShadow(textRenderer, Text.literal("AFK Messages"), width / 2 - 150, 110, 0xffffff);
    }

    @Override
    public void close() {
        config.save();
        client.setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
