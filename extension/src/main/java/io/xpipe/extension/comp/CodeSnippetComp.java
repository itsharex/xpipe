package io.xpipe.extension.comp;

import io.xpipe.fxcomps.Comp;
import io.xpipe.fxcomps.CompStructure;
import io.xpipe.fxcomps.util.PlatformUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;


public class CodeSnippetComp extends Comp<CompStructure<StackPane>> {

    private final ObservableValue<Boolean> showLineNumbers;
    private final ObservableValue<CodeSnippet> value;

    public CodeSnippetComp(boolean showLineNumbers, ObservableValue<CodeSnippet> value) {
        this.showLineNumbers = new SimpleBooleanProperty(showLineNumbers);
        this.value = PlatformUtil.wrap(value);
    }

    public CodeSnippetComp(ObservableValue<Boolean> showLineNumbers, ObservableValue<CodeSnippet> value) {
        this.showLineNumbers = PlatformUtil.wrap(showLineNumbers);
        this.value = PlatformUtil.wrap(value);
    }

    private static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void fillArea(VBox lineNumbers, InlineCssTextArea s) {
        lineNumbers.getChildren().clear();
        s.clear();

        int number = 1;
        for (CodeSnippet.Line line : value.getValue().lines()) {
            var numberLabel = new Label(String.valueOf(number));
            numberLabel.getStyleClass().add("line-number");
            lineNumbers.getChildren().add(numberLabel);

            for (var el : line.elements()) {
                String hex = toRGBCode(el.color());
                s.append(el.text(), "-fx-fill: " + hex + ";");
            }

            boolean last = number == value.getValue().lines().size();
            if (!last) {
                s.appendText("\n");
            }

            number++;
        }
    }

    private Region createCopyButton(Region container) {
        var button = new Button();
        button.setGraphic(new FontIcon("mdoal-content_copy"));
        button.setOnAction(e -> {
            var string = new StringSelection(value.getValue().getRawString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(string, string);
        });
        button.getStyleClass().add("copy");
        button.getStyleClass().add("button-comp");
        button.visibleProperty().bind(container.hoverProperty());
        return button;
    }

    @Override
    public CompStructure<StackPane> createBase() {
        var s = new InlineCssTextArea();
        s.setEditable(false);
        s.setBackground(null);
        s.getStyleClass().add("code-snippet");
        s.addEventFilter(ScrollEvent.ANY, e -> {
            s.getParent().fireEvent(e);
            e.consume();
        });

        var lineNumbers = new VBox();
        lineNumbers.getStyleClass().add("line-numbers");
        fillArea(lineNumbers, s);
        value.addListener((c,o,n) -> {
            fillArea(lineNumbers, s);
        });

        var spacer = new Region();
        spacer.getStyleClass().add("spacer");

        var content = new HBox(s);
        spacer.prefHeightProperty().bind(content.heightProperty());
        if (showLineNumbers.getValue()) {
            content.getChildren().add(0, lineNumbers);
            content.getChildren().add(1, spacer);
        }
        showLineNumbers.addListener((c,o,n) -> {
            if (n) {
                content.getChildren().add(0, lineNumbers);
                content.getChildren().add(1, spacer);
            } else {
                content.getChildren().remove(lineNumbers);
                content.getChildren().remove(spacer);
            }
        });
        HBox.setHgrow(s, Priority.ALWAYS);
        var container = new ScrollPane(content);
        container.setFitToWidth(true);
        container.setFitToHeight(true);

        var c = new StackPane(container);
        container.prefHeightProperty().bind(c.heightProperty());
        c.getStyleClass().add("code-snippet-container");

        var copyButton = createCopyButton(c);
        var pane = new AnchorPane(copyButton);
        pane.setPickOnBounds(false);
        AnchorPane.setTopAnchor(copyButton, 10.0);
        AnchorPane.setRightAnchor(copyButton, 10.0);
        c.getChildren().add(pane);

        return new CompStructure<>(c);
    }
}
