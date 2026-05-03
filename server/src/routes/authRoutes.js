import express from 'express';
import { startRegister, login, forgotPassword, checkPhone } from '../controllers/authController.js';


const router = express.Router();

router.post('/check-phone', checkPhone);
router.post('/start-register', startRegister);
router.post('/login', login);
router.post('/forgot-password', forgotPassword);

export default router;