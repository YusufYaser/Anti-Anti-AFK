package xyz.yusufyaser.aaafk.UI;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class NumberFieldWidget extends TextFieldWidget {
    private int max = 0;

    public NumberFieldWidget(TextRenderer textRenderer, int width, int height, Text text) {
        super(textRenderer, width, height, text);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Character.isDigit(chr)) {
            if (max != 0 && !this.getText().isEmpty() && Integer.parseInt(this.getText() + chr) > max) return false;
            return super.charTyped(chr, modifiers);
        }

        return false;
    }

    public void setMax(int max) {
        this.max = max;
    }
}

