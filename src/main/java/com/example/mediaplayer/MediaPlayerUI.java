package com.example.mediaplayer;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class MediaPlayerUI extends Application {
    @FXML
    private ListView<MP3File> playList;
    private static final String SONG_DIRECTORY = "songs/";

    //MEDIA VIEWER VARIABLES
    @FXML
    private MediaView mview;
    private Media visualize_video;
    private MediaPlayer mpVideo;

    @FXML
    private Label durationLabel;
    @FXML
    private Label staticDurationLabel;
    @FXML
    private Label isPlayingLabel;
    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MediaPlayer mediaPlayer = null;
    @FXML
    private Slider timeSlider;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Button pauseButton;
    @FXML
    private Button playButton;
    @FXML
    private Button restartButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private ImageView floorIcon;

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private HBox topBox;
    private MP3File currentPlayingSong;

    private int currentSongIndex = 0;

    boolean singleClick = true;


    private int IV_SIZE = 40;
    private int playListSize;
    private boolean isPlaying = false;
    MP3File currentSelection = null;
    private double currentVolume = 0.0;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MediaPlayer.fxml"));
            Parent root = loader.load();
            playList = (ListView) loader.getNamespace().get("playList");
            mview = (MediaView) loader.getNamespace().get("mview"); //Media view object
            floorIcon = (ImageView) loader.getNamespace().get("floorIcon"); //Application Icon
            topBox = (HBox) loader.getNamespace().get("topBox");
            timeSlider = (Slider) loader.getNamespace().get("timeSlider");
            volumeSlider = (Slider) loader.getNamespace().get("volumeSlider");

            durationLabel = (Label) loader.getNamespace().get("durationLabel");
            staticDurationLabel = (Label) loader.getNamespace().get("staticDurationLabel");
            isPlayingLabel = (Label) loader.getNamespace().get("isPlayingLabel");
            aboutMenuItem = (MenuItem) loader.getNamespace().get("aboutMenuItem");

            //Playback buttons
            pauseButton = (Button) loader.getNamespace().get("pauseButton");
            playButton = (Button) loader.getNamespace().get("playButton");
            restartButton = (Button) loader.getNamespace().get("restartButton");
            previousButton = (Button) loader.getNamespace().get("previousButton");
            nextButton = (Button) loader.getNamespace().get("nextButton");

            Image play_Image = new Image(getClass().getResource("/com/example/mediaplayer/player_play.png").toString());
            ImageView playView = new ImageView(play_Image);
            playView.setFitHeight(IV_SIZE);
            playView.setFitWidth(IV_SIZE);

            Image pause_Image = new Image(getClass().getResource("/com/example/mediaplayer/player_pause.png").toString());
            ImageView pauseView = new ImageView(pause_Image);
            pauseView.setFitHeight(IV_SIZE);
            pauseView.setFitWidth(IV_SIZE);

            Image restart_Image = new Image(getClass().getResource("/com/example/mediaplayer/player_restart.png").toString());
            ImageView restartView = new ImageView(restart_Image);
            restartView.setFitHeight(IV_SIZE);
            restartView.setFitWidth(IV_SIZE);

            Image nextImage = new Image(getClass().getResource("/com/example/mediaplayer/player_skip.png").toString());
            ImageView nextView = new ImageView(nextImage);
            nextView.setFitHeight(IV_SIZE);
            nextView.setFitWidth(IV_SIZE);

            Image prevImage = new Image(getClass().getResource("/com/example/mediaplayer/player_previous.png").toString());
            ImageView previousView = new ImageView(prevImage);
            previousView.setFitHeight(IV_SIZE);
            previousView.setFitWidth(IV_SIZE);

            Image floorImage = new Image(getClass().getResource("/com/example/mediaplayer/floor.png").toString());
            ImageView floorView = new ImageView(floorImage);



            //Setting graphics for buttons and widgets
            playButton.setGraphic(playView);
            pauseButton.setGraphic(pauseView);
            restartButton.setGraphic(restartView);
            previousButton.setGraphic(previousView);
            nextButton.setGraphic(nextView);
            floorIcon.setImage(floorImage);




        playList.setCellFactory(param -> new ListCell<MP3File>() {
                @Override
                protected void updateItem(MP3File item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-text-fill: green; -fx-background-color: black;");
                    } else {
                        setText(item.getName());
                        setStyle("-fx-text-fill: green; -fx-background-color: black;");
                        if (isSelected()) {
                            setStyle("-fx-text-fill: green; -fx-background-color: white;");
                        } else {
                            setStyle("-fx-text-fill: green; -fx-background-color: black;");
                        }
                    }
                }
            });


            MenuItem uploadSong = (MenuItem) loader.getNamespace().get("uploadSong");
            uploadSong.setOnAction(this::uploadMP3File);
            double prefWidth = root.prefWidth(-1);
            double prefHeight = root.prefHeight(-1);
            initalizeMediaControls();
            Scene scene = new Scene(root, prefWidth, prefHeight);
           // scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.show();
            refreshSongList();
            aboutMenuItem.setOnAction(event -> openAboutPopup());

            visualizer();
            //scene.xProperty().bind(primaryStage.xProperty());
            //scene.yProperty().bind(primaryStage.yProperty());
    }

    private String formattedDuration(Duration duration) {
        long totalSeconds = (long) duration.toSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void openAboutPopup() {
        Alert aboutPopup = new Alert(Alert.AlertType.INFORMATION);
        aboutPopup.setTitle("About");
        aboutPopup.setHeaderText("Floor Media Player");
        aboutPopup.setContentText("Made by: Moses Lemma & Rajiv Naidu");
        aboutPopup.showAndWait();
    }

    private void uploadMP3File(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String mp3FileName = selectedFile.getName();
            String mp3FilePath = selectedFile.getAbsolutePath();

            saveSong(selectedFile);
            refreshSongList();
            MP3File mp3File = new MP3File(mp3FileName, mp3FilePath);
            //playList.getItems().add(mp3File);

        }

    }

    Timeline clickTimeline = new Timeline(
            new KeyFrame(Duration.millis(300), e -> {
                if (singleClick) {
                    // Single click action (Restart Song)
                    restartCurrentSong();
                }
                singleClick = true;
            })
    );

    private void pauseCurrentSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mpVideo.pause();
        }

    }
    private void playCurrentSong() {
        if (currentPlayingSong != null) {
            mediaPlayer.play();
            mpVideo.play();
            isPlaying=true;
        }
    }

    private void restartCurrentSong() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO);
            mpVideo.seek(Duration.ZERO);
            currentSongIndex--;
        }
    }

    private void nextSong() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO);
            mpVideo.seek(Duration.ZERO);
            currentSongIndex++;
        }
    }
    private void previousSong() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO);
            mpVideo.seek(Duration.ZERO);
        }
    }

    private void highlightCurrentlyPlayingSong(MP3File currentPlayingSong) {
        playList.getItems().forEach(mp3File -> {
            if (mp3File.equals(currentPlayingSong)) {
                playList.getSelectionModel().select(mp3File);
            }
        });
    }


    private void initalizeMediaControls() {
        playButton.setOnAction(event -> {
            if (currentPlayingSong != null) {
                playCurrentSong();
            } else  {
                MP3File firstSong = playList.getItems().get(currentSongIndex);
                if (firstSong != null) {
                    playMP3(firstSong.getPath(), firstSong.getName());
                    highlightCurrentlyPlayingSong(firstSong);
                }
            }


        });

        pauseButton.setOnAction(event -> {
            if (currentPlayingSong != null) {
                if (isPlaying) {
                    pauseCurrentSong();
                }
            }
            highlightCurrentlyPlayingSong(currentPlayingSong);
        });

        restartButton.setOnAction(event -> {
            if (currentPlayingSong != null) {
                restartCurrentSong();
            }
        });

        //PLAYING PREVIOUS SONG
        previousButton.setOnMouseClicked(event -> {
            if (currentPlayingSong != null) {
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    MP3File previousSong = playList.getItems().get(currentSongIndex);
                    if (previousSong != null) {
                        playMP3(previousSong.getPath(), previousSong.getName());
                        highlightCurrentlyPlayingSong(previousSong);
                    }
                }
                else {
                    MP3File previousSong = playList.getItems().get(currentSongIndex);
                    if (previousSong != null) {
                        playMP3(previousSong.getPath(), previousSong.getName());
                        highlightCurrentlyPlayingSong(previousSong);

                    }
                }
            }
        });

        //PLAYING NEXT SONG
        nextButton.setOnMouseClicked(event -> {
            currentSongIndex++;
            if (currentSongIndex != playList.getItems().size()) {
                MP3File nextSong = playList.getItems().get(currentSongIndex);
                if (nextSong != null) {
                    playMP3(nextSong.getPath(), nextSong.getName());
                    highlightCurrentlyPlayingSong(nextSong);
                }
            } else {
                currentSongIndex = 0;
                MP3File nextSong = playList.getItems().get(currentSongIndex);
                playMP3(nextSong.getPath(), nextSong.getName());
                highlightCurrentlyPlayingSong(nextSong);
            }
        });



        playList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) { // Detect a double-click
                MP3File selectedMP3 = playList.getSelectionModel().getSelectedItem();
                mpVideo.play();
                if (selectedMP3 != null) {
                    if (selectedMP3.equals(currentPlayingSong) && isPlaying) {
                        pauseCurrentSong();
                        } else {
                        playMP3(selectedMP3.getPath(), selectedMP3.getName());
                        currentSongIndex = playList.getSelectionModel().getSelectedIndex();
                    }
                }
            }
        });



    }

    private void refreshSongList() {
        File songsDirectory = new File(SONG_DIRECTORY);
        if(!songsDirectory.exists()) {
            System.err.println("Songs directory doesn't exits.");
            return;
        }

        File[] songFiles = songsDirectory.listFiles((dir,name) -> name.endsWith(".mp3"));
        if (songFiles != null) {
            playList.getItems().clear();
            for (File songFile : songFiles) {
                MP3File mp3File = new MP3File(songFile.getName(), songFile.getAbsolutePath());
                playList.getItems().add(mp3File);
                //System.out.print(numberSongs);
            }
        }
    }
    private void playMP3(String mp3FilePath, String mp3FileName) {
        //System.out.println(mp3FileName);
        //System.out.println(mp3FilePath);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Media media = new Media(new File(mp3FilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(currentVolume);

        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == MediaPlayer.Status.PLAYING) {
                isPlayingLabel.setText("Playing");
            } else if (newValue == MediaPlayer.Status.PAUSED) {
                isPlayingLabel.setText("Paused");
            } else if (newValue == MediaPlayer.Status.STOPPED) {
                isPlayingLabel.setText("N/A");
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!timeSlider.isValueChanging()) {
                Duration duration = media.getDuration();
                System.out.println(duration.toSeconds());
                timeSlider.setMax(duration.toSeconds());
                timeSlider.setValue(newValue.toSeconds());
                String staticDuration = formattedDuration(duration);
                staticDurationLabel.setText(staticDuration);
                Timeline labelUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    String formattedDuration = formattedDuration(currentTime);
                    durationLabel.setText(formattedDuration);
                }));
                labelUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
                labelUpdateTimeline.play();
            }
        });

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (timeSlider.isValueChanging()) {
                mediaPlayer.seek(new Duration(newValue.doubleValue() * 1000));
            }
        });

        timeSlider.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double totalWidth = timeSlider.getWidth();
                double mouseX = event.getX();
                double newVolume = (mouseX / totalWidth) * 100.0;

                mediaPlayer.seek(new Duration(newVolume * 1000));
                double newPosition = (mouseX / totalWidth) * timeSlider.getMax();
                mediaPlayer.seek(new Duration(newPosition * 1000));
            }
        });
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (volumeSlider.isValueChanging()) {
                currentVolume = newValue.doubleValue() / 100.0;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(currentVolume);
                }
                //double volume = volumeSlider.getValue() / 100.0;
                //mediaPlayer.setVolume(volume);
            }
        });
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!timeSlider.isValueChanging()) {
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                timeSlider.setValue(currentTime);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        mediaPlayer.setOnEndOfMedia(() -> {
            currentSongIndex++;
            System.out.print(currentSongIndex);
            if (currentSongIndex == playList.getItems().size()) {
                currentSongIndex = 0;
            }

            MP3File nextSongPlaying = playList.getItems().get(currentSongIndex);
            playMP3(nextSongPlaying.getPath(), nextSongPlaying.getName());

        });

        //volumeSlider.valueProperty().bindBidirectional(mediaPlayer.volumeProperty());

        currentPlayingSong = new MP3File(mp3FileName, mp3FilePath);
        System.out.print(mp3FileName);
        isPlaying = true;
        mediaPlayer.play();

    }

    private void handleVolumeChange(double newVolume) {
        currentVolume = newVolume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(currentVolume / 100.0);
        }
    }

    @FXML
    private void topBox_dragged(MouseEvent event) {
        Stage stage = (Stage) topBox.getScene().getWindow();
        stage.setY(event.getScreenY() - yOffset);
        stage.setX(event.getScreenX() - xOffset);
    }

    @FXML
    private void topBox_pressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void saveSong(File selectedFile) {
        File songsDirectory = new File(SONG_DIRECTORY);
        if (!songsDirectory.exists()) {
            if(songsDirectory.mkdirs()) {
                System.out.println("Directory created: " + SONG_DIRECTORY);
            } else {
                System.err.println("Failed to create directory" + SONG_DIRECTORY);
                return;
            }
        }
        File destination = new File(SONG_DIRECTORY + selectedFile.getName());
        int suffix = 1;

        // Append a suffix if the file already exists
        while (destination.exists()) {
            String originalName = selectedFile.getName();
            int dotIndex = originalName.lastIndexOf(".");
            String baseName = dotIndex >= 0 ? originalName.substring(0, dotIndex) : originalName;
            String extension = dotIndex >= 0 ? originalName.substring(dotIndex) : "";

            String newName = baseName + " (" + suffix + ")" + extension;
            destination = new File(SONG_DIRECTORY + newName);
            suffix++;
        }
        try {
            //File destination = new File(SONG_DIRECTORY + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destination.toPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class MP3File {
        private String name;
        private String path;

        public MP3File(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }


    public void visualizer() {
        String resourcePath = "/com/example/mediaplayer/visualizations.mp4";
        URL resourceUrl = getClass().getResource(resourcePath);

        if (resourceUrl != null) {
            visualize_video = new Media(resourceUrl.toString());
            mpVideo = new MediaPlayer(visualize_video);
            mpVideo.setMute(true);
            mview.setMediaPlayer(mpVideo);
        } else {
            System.err.println("Resource not found: " + resourcePath);
        }
        //visualize_video = new Media(new File("resources/visualizations.mp4").toURI().toString());
        //mpVideo = new MediaPlayer(visualize_video);
        //mpVideo.setMute(true);
        //mview.setMediaPlayer(mpVideo);
    }
    public void closePlayer(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void minimizePlayer(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }


}
