# Safedrony Model Training Environment

이 문서는 **YOLOv5 모델 학습 및 TFLite 변환 환경 설정** 방법을 안내합니다.

---

## ✅ Conda 가상환경 설정

```bash
# 현재 가상환경 종료
conda deactivate

# Python 3.8 기반 가상환경 생성
conda create -n safedrony python=3.8

# 가상환경 활성화
conda activate safedrony

# PyTorch & torchvision
pip install torch torchvision

# TensorFlow (TFLite 변환용)
pip install tensorflow==2.9.1

# 기타 라이브러리
pip install onnx onnx-tf opencv-python numpy
