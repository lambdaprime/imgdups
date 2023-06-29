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

import id.imgdups.finddups.FindDupsSettings;
import id.xfunction.text.Ellipsizer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * This panels looks like:
 *
 * <pre>
 * |------------|
 * |   IMAGE    |
 * |  file path |
 * | resolution |
 * |     size   |
 * --------------
 * </pre>
 * 
 * @author lambdaprime <intid@protonmail.com>
 */
public class ImageDetailsPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private FindDupsSettings settings = FindDupsSettings.getInstance();
    private Path imageFile;
    private JLabel imageView;
    private JButton deleteButton;
    private int imageHeight;
    private int imageWidth;
    private double fileSize;
    private JLabel fileSizeView;
    private JLabel resolutionView;

    public ImageDetailsPanel(Path imageFile) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.imageFile = imageFile;
    }

    public double getFileSize() {
        return fileSize;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setup() {
        try {
            var img = ImageIO.read(imageFile.toFile());
            imageHeight = img.getHeight();
            imageWidth = img.getWidth();
            var resolution = String.format("%dx%d px", imageWidth, imageHeight);
            img = resizeImage(img, settings.getSize(), settings.getSize());

            imageView = new JLabel(new ImageIcon(img));
            imageView.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageView.addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            try {
                                Desktop.getDesktop().open(imageFile.toFile());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
            add(imageView);

            var fileNameView = new JLabel(new Ellipsizer(30).ellipsizeHead(imageFile.toString()));
            fileNameView.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(fileNameView);

            resolutionView = new JLabel(resolution);
            resolutionView.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(resolutionView);
            fileSize = Files.size(imageFile) / 1024. / 1024.;
            String fileSizeCaption = String.format("%f MB", fileSize);

            fileSizeView = new JLabel(fileSizeCaption);
            fileSizeView.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(fileSizeView);

            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            deleteImageFile();
                        }
                    });
            deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(deleteButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {}

    /**
     * Resize image preserving its proportions. Areas which are left blank after the resize will be
     * black. Image will be centered.
     */
    private BufferedImage resizeImage(BufferedImage image, int height, int width) {
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = outImage.createGraphics();
        int w = 0;
        int h = 0;
        if (image.getWidth() > image.getHeight()) {
            float r = (float) image.getWidth() / image.getHeight();
            w = width;
            h = (int) (width / r);
        } else {
            float r = (float) image.getHeight() / image.getWidth();
            h = height;
            w = (int) (height / r);
        }
        g.drawImage(image, (width - w) / 2, (height - h) / 2, w, h, null);
        return outImage;
    }

    public void deleteImageFile() {
        if (!deleteButton.isEnabled()) return;
        try {
            Files.delete(imageFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        imageView.setIcon(UIManager.getIcon("FileView.fileIcon"));
        deleteButton.setEnabled(false);
    }

    public void hightlightFileSize() {
        fileSizeView.setForeground(Color.blue);
    }

    public void hightlightResolution() {
        resolutionView.setForeground(Color.blue);
    }
}
