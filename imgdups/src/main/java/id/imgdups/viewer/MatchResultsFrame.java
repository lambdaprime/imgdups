/*
 * Copyright 2021 jrosclient project
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import id.opencvkit.feature.match.MatchResult;

public class MatchResultsFrame extends JFrame implements ActionListener, Runnable {

    private static final long serialVersionUID = 1L;
    private List<MatchResult<Path>> matches;
    private int cursor;

    public MatchResultsFrame(List<MatchResult<Path>> matches) {
        this.matches = matches.stream()
                .filter(Predicate.not(MatchResult::isIdentical))
                .collect(Collectors.toList());
    }

    private void showNext() {
        MatchResult<Path> matchResult = matches.get(cursor++);
        while (!Files.exists(matchResult.getA()) || !Files.exists(matchResult.getB())) {
            if (cursor == matches.size()) return;
            matchResult = matches.get(cursor++);
        }
        System.out.println(matchResult);
        JPanel rootPanel = new JPanel(new BorderLayout());
        
        var imageA = new ImageDetailsPanel(matchResult.getA());
        imageA.setup();

        var imageB = new ImageDetailsPanel(matchResult.getB());
        imageB.setup();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(.5);
        splitPane.add(imageA);
        splitPane.add(imageB);
        rootPanel.add(splitPane, BorderLayout.CENTER);
        
        var nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNext();
            }
        });
        if (cursor < matches.size()) {
            rootPanel.add(nextButton, BorderLayout.SOUTH);
        }
        setContentPane(rootPanel);
        revalidate();
    }

    @Override
    public void run() {
        if (cursor >= matches.size()) return;
        setTitle("Duplicate images");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 700);
        setLayout(new BorderLayout());
        showNext();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
    }

}
