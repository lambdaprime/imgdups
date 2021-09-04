/*
 * Copyright 2021 imgdups project
 * 
 * Website: https://github.com/lambdaprime/jrosclient
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
/*
 * Authors:
 * - lambdaprime <intid@protonmail.com>
 */
package id.imgdups.viewer;

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

import id.opencvkit.feature.match.MatchResult;
import id.xfunction.lang.XThread;

public class MatchResultsFrame extends JFrame implements ActionListener, Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    private List<MatchResult<Path>> matches;
    private int cursor;
    private ImageDetailsPanel imageA;
    private ImageDetailsPanel imageB;

    public MatchResultsFrame(List<MatchResult<Path>> matches) {
        this.matches = matches.stream()
                .filter(Predicate.not(MatchResult::isIdentical))
                .collect(Collectors.toList());
    }

    private void showNext() {
        removeKeyListener(this);
        MatchResult<Path> matchResult = matches.get(cursor++);
        while (!Files.exists(matchResult.getA()) || !Files.exists(matchResult.getB())) {
            if (cursor == matches.size()) return;
            matchResult = matches.get(cursor++);
        }
        System.out.println(matchResult);
        JPanel rootPanel = new JPanel(new BorderLayout());
        
        imageA = new ImageDetailsPanel(matchResult.getA());
        imageA.setup();

        imageB = new ImageDetailsPanel(matchResult.getB());
        imageB.setup();

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
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNext();
            }
        });
        var label = new JLabel("You can use left/right arrows to delete left/right image and to move to the next match");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(label);
        if (cursor < matches.size()) {
            bottomPanel.add(nextButton);
        }
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);
        revalidate();
        addKeyListener(this);
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
    public void actionPerformed(ActionEvent arg0) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

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
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
