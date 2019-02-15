<?php

class DbOperation
{
    private $con;

    function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        $db = new DbConnect();
        $this->con = $db->connect();
    }

    //region Users
    function login($email, $pass)
    {
        $stmt = $this->con->query("SELECT * FROM users WHERE email = '$email' AND password = '$pass'");
        if($stmt->num_rows > 0) return $stmt->fetch_assoc();
        return null;
    }

    function setUser($data)
    {

        $stmt = $this->con->prepare("INSERT INTO users (name, email, password, number, address) VALUES (?, ?, ?, ?, ?)");
        $stmt->bind_param("sssss", $data['name'], $data['email'], $data['password'], $data['number'], $data['address']);

        if($this->isUserExist($data['email'])) return USER_EXIST;
        if ($stmt->execute()) return USER_CREATED;
        return USER_CREATION_FAILED;
    }
    //endregion

    //region Artworks
    function setArtwork($data, $artFile)
    {
        $artUrl = 'http://' . gethostbyname(gethostname()) . API_PATH . ARTWORKS_PATH . $artFile;
        $stmt = $this->con->prepare("INSERT INTO artworks (userId, deviceName, artworkName, author, date, description, artworkUrl)
        VALUES (?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("issssss", $data['userId'], $data['deviceName'], $data['artworkName'], $data['author'], $data['date'], $data['description'], $artUrl);
        if ($stmt->execute())
            return ARTWORK_CREATED;
        return ARTWORK_CREATION_FAILED;
    }

    function getArtworks()
    {
        $artworks = array();
        $sql = "SELECT * from artworks ORDER BY artworkId DESC;";
        $stmt = $this->con->query($sql);
        while($row = $stmt->fetch_assoc()){array_push($artworks, $row);}
        return $artworks;
    }

    function getArtworksByName($artworkName)
    {
        $artworks = array();
        $sql = "SELECT * from artworks
        WHERE artworkName LIKE '%$artworkName%' 
        ORDER BY p.productId DESC;";
        $stmt = $this->con->query($sql);
        while($row = $stmt->fetch_assoc()){array_push($artworks, $row);}
        return $artworks;

    }
    //endregion

    //region Methods

    function isUserExist($email)
    {
        $stmt = $this->con->query("SELECT * FROM users WHERE email = '$email'");
        return $stmt->num_rows > 0;
    }
    //endregion
}