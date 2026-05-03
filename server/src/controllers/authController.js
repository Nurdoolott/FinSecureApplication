import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import prisma from '../config/prisma.js';
import { verifyFirebaseToken } from '../services/firebaseOtpService.js';

export const startRegister = async (req, res) => {
  try {
    const { fullName, password, email, firebaseToken } = req.body;

    if (!fullName || !password || !firebaseToken) {
      return res.status(400).json({ message: 'fullName, password and firebaseToken are required' });
    }

    if (password.length < 6) {
      return res.status(400).json({ message: 'Password must be at least 6 characters long' });
    }

    const decoded = await verifyFirebaseToken(firebaseToken);
    const phoneNumber = decoded.phone_number;

    const existingUser = await prisma.user.findUnique({ where: { phoneNumber } });
    if (existingUser) {
      return res.status(409).json({ message: 'Phone number is already registered' });
    }

    if (email) {
      const existingEmail = await prisma.user.findUnique({ where: { email } });
      if (existingEmail) {
        return res.status(409).json({ message: 'Email is already in use' });
      }
    }

    const passwordHash = await bcrypt.hash(password, 10);
    const accountNumber = `ACC${Date.now()}${Math.floor(Math.random() * 1000)}`;

    const user = await prisma.user.create({
      data: {
        fullName,
        phoneNumber,
        email: email || null,
        passwordHash,
        account: { create: { accountNumber, balance: 1000, currency: 'USD' } }
      },
      include: { account: true }
    });

    return res.status(201).json({
      message: 'Registration completed successfully',
      user: {
        id: user.id,
        fullName: user.fullName,
        phoneNumber: user.phoneNumber,
        email: user.email,
        account: user.account
      }
    });
  } catch (error) {
    console.error('REGISTER ERROR:', error);
    return res.status(500).json({ message: 'Server error during registration' });
  }
};

export const login = async (req, res) => {
  try {
    const { phoneNumber, password } = req.body;

    if (!phoneNumber || !password) {
      return res.status(400).json({ message: 'Phone number and password are required' });
    }

    const user = await prisma.user.findUnique({
      where: { phoneNumber },
      include: { account: true }
    });

    if (!user) {
      return res.status(401).json({ message: 'Phone number or password incorrect' });
    }

    const isPasswordCorrect = await bcrypt.compare(password, user.passwordHash);
    if (!isPasswordCorrect) {
      return res.status(401).json({ message: 'Phone number or password incorrect' });
    }

    const token = jwt.sign(
      { userId: user.id, phoneNumber: user.phoneNumber },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );

    return res.status(200).json({
      message: 'Login successful',
      token,
      user: {
        id: user.id,
        fullName: user.fullName,
        phoneNumber: user.phoneNumber,
        email: user.email,
        account: user.account
      }
    });
  } catch (error) {
    console.error('LOGIN ERROR:', error);
    return res.status(500).json({ message: 'Server error during login' });
  }
};

export const forgotPassword = async (req, res) => {
  try {
    const { firebaseToken, newPassword } = req.body;

    if (!firebaseToken || !newPassword) {
      return res.status(400).json({ message: 'firebaseToken and newPassword are required' });
    }

    if (newPassword.length < 6) {
      return res.status(400).json({ message: 'Password must be at least 6 characters long' });
    }

    const decoded = await verifyFirebaseToken(firebaseToken);
    const phoneNumber = decoded.phone_number;

    const user = await prisma.user.findUnique({ where: { phoneNumber } });
    if (!user) {
      return res.status(404).json({ message: 'User with this phone number was not found' });
    }

    const passwordHash = await bcrypt.hash(newPassword, 10);
    await prisma.user.update({ where: { phoneNumber }, data: { passwordHash } });

    return res.status(200).json({ message: 'Password has been reset successfully' });
  } catch (error) {
    console.error('FORGOT PASSWORD ERROR:', error);
    return res.status(500).json({ message: 'Server error during password reset' });
  }
};