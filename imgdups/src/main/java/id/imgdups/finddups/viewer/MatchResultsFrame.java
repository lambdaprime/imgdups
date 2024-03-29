/*
 * Copyright 2021 imgdups project
 * 
 * Website: https://github.com/lambdaprime
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.imgdups.finddups.viewer;

import id.matcv.feature.match.MatchResult;
import id.xfunction.lang.XThread;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * @author lambdaprime <intid@protonmail.com>
 */
public class MatchResultsFrame extends JFrame implements ActionListener, Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    private List<MatchResult<Path>> matches;
    private int cursor;
    private ImageDetailsPanel imageA;
    private ImageDetailsPanel imageB;

    public MatchResultsFrame(List<MatchResult<Path>> matches) {
        this.matches =
                matches.stream()
                        .filter(Predicate.not(MatchResult::isIdentical))
                        .collect(Collectors.toList());
    }

    private void showNext() {
        if (cursor == matches.size()) return;
        removeKeyListener(this);
        MatchResult<Path> matchResult = matches.get(cursor);
        System.out.println(matchResult);
        JPanel rootPanel = new JPanel(new BorderLayout());

        imageA = new ImageDetailsPanel(matchResult.getA());
        imageA.setup();

        imageB = new ImageDetailsPanel(matchResult.getB());
        imageB.setup();

        if (imageA.getFileSize() < imageB.getFileSize()) imageB.hightlightFileSize();
        else imageA.hightlightFileSize();

        if (imageA.getImageHeight() < imageB.getImageHeight()
                && imageA.getImageWidth() < imageB.getImageWidth()) imageB.hightlightResolution();
        if (imageB.getImageHeight() < imageA.getImageHeight()
                && imageB.getImageWidth() < imageA.getImageWidth()) imageA.hightlightResolution();

        JPanel topPanel = buildTopPanel();
        rootPanel.add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(.5);
        splitPane.add(imageA);
        splitPane.add(imageB);
        rootPanel.add(splitPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        var nextButton = new JButton("Next");
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showNext();
                    }
                });
        var label =
                new JLabel(
                        "<html>You can use left/right arrows to delete left/right image and to move"
                                + " to the next match<br>Use space to move Next</html>");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(label);
        while (++cursor < matches.size()) {
            var nextResult = matches.get(cursor);
            if (Files.exists(nextResult.getA()) && Files.exists(nextResult.getB())) {
                break;
            }
        }
        if (cursor < matches.size()) {
            bottomPanel.add(nextButton);
        }
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);
        revalidate();
        addKeyListener(this);
    }

    private JPanel buildTopPanel() {
        var topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        var label = new JLabel(String.format("Match %d out of %d", cursor + 1, matches.size()));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(label);
        return topPanel;
    }

    private void showNextWithDelay() {
        if (cursor == matches.size()) return;
        XThread.sleep(1000);
        showNext();
    }

    @Override
    public void run() {
        if (cursor >= matches.size()) return;
        setTitle("Duplicate images");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 700);
        setLayout(new BorderLayout());
        showNext();
        setFocusable(true);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            imageA.deleteImageFile();
            ForkJoinPool.commonPool().submit(this::showNextWithDelay);
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            imageB.deleteImageFile();
            ForkJoinPool.commonPool().submit(this::showNextWithDelay);
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            showNext();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
