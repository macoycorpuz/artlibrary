<?php

use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;
use \Slim\Http\UploadedFile;

require '../vendor/autoload.php';
require_once '../includes/DbOperation.php';

$app = new \Slim\App(['settings' => ['displayErrorDetails' => true]]);
$container = $app->getContainer();
$container['artworks_directory'] = __DIR__ . '/../images/artworks/';
$responseData = array();

//region Users
$app->get('/login/email/{email}/password/{password}', function (Request $request, Response $response) {
    $email = $request->getAttribute('email');
    $password = $request->getAttribute('password');
    $db = new DbOperation();
    $user = $db->login($email, $password);
    if ($user != null) {
        $responseData['error'] = false;
        $responseData['user'] = $user;
    } else {
        $responseData['error'] = true;
        $responseData['message'] = 'Invalid email or password';
    }

    $response->getBody()->write(json_encode($responseData));
});
$app->post('/users', function (Request $request, Response $response) {

    $user = array('name', 'email', 'password', 'number', 'address');

    $requestData = $request->getParsedBody();
    if(!isEmptyField($user)) return;
    $db = new DbOperation();
    $result = $db->setUser($requestData);
    $responseData = array();
    if ($result == USER_CREATED) {
        $responseData['error'] = false;
        $responseData['message'] = 'Registered successfully';
    } elseif ($result == USER_CREATION_FAILED) {
        $responseData['error'] = true;
        $responseData['message'] = 'Unable to register user';
    } elseif ($result == USER_EXIST) {
        $responseData['error'] = true;
        $responseData['message'] = 'This email already exist';
    }  

    $response->getBody()->write(json_encode($responseData)); 
});
//endregion

//region Artworks
$app->post('/artworks', function (Request $request, Response $response) {
    $artwork = array('artworkName', 'description', 'author', 'date', 'deviceName', 'userId');
    if (!isEmptyField($artwork)) return;
    if(!$_FILES["artworkImage"]){
        $imageData = array();
        $imageData['error'] = true;
        $imageData['message'] = 'Image upload error';
        $response->getBody()->write(json_encode($imageData));
        return;
    }

    $directory = $this->get('artworks_directory');
    $artworkFile = getFileName();
    $requestData = $request->getParsedBody();
    $db = new DbOperation();
    $result = $db->setArtwork($requestData, $artworkFile);
    $responseData = array();
    if ($result == ARTWORK_CREATED) {
        move_uploaded_file($_FILES["artworkImage"]["tmp_name"], $directory.$artworkFile);
        $responseData['error'] = false;
        $responseData['message'] = 'Artwork has been posted';
    } elseif ($result == ARTWORK_CREATION_FAILED) {
        $responseData['error'] = true;
        $responseData['message'] = 'Unable to post artwork';
    }    

    $response->getBody()->write(json_encode($responseData));
});
$app->get('/artworks', function (Request $request, Response $response) {
    $db = new DbOperation();
    $artworks = $db->getArtworks();
    $responseData = array();
    if($artworks != []) {
        $responseData['error'] = false;
        $responseData['message'] = 'Artworks found';
        $responseData['artworks'] = $artworks;
    } else {
        $responseData['error'] = true;
        $responseData['message'] = 'No artwork/s found';
    }
    $response->getBody()->write(json_encode($responseData)); 
});
$app->get('/artworks/artworkName/{artworkName}', function (Request $request, Response $response) {
    $artworkName = $request->getAttribute('artworkName');
    $db = new DbOperation();
    $artworks = $db->getArtworksByName($artworkName);
    $responseData = array();
    if($artworks != []) {
        $responseData['error'] = false;
        $responseData['artworks'] = $artworks;
    } else {
        $responseData['error'] = true;
        $responseData['message'] = 'No artwork/s found';
    }
    $response->getBody()->write(json_encode($responseData)); 
});
//endregion

//region Methods
function isEmptyField($required_fields)
{
    $error = false;
    $error_fields = "";
    $request_params = $_REQUEST;

    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }

    //$error = true;
    if ($error) {
        $response = array();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echo json_encode($response);
        return false;
    }
    return true;
}

function getFileName()
{
    $extension = "jpg";
    $basename = bin2hex(random_bytes(8)); 
    $filename = sprintf('%s.%0.8s', $basename, $extension);

    return $filename;
}
//endregion

$app->run();