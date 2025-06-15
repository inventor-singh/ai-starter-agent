import openai
from openai import OpenAI
import os

OPENAI_API_KEY = os.environ.get('OPENAI_API_KEY')
OPENAI_API_BASE='https://proxy.fuelix.ai'

client = OpenAI(api_key=OPENAI_API_KEY, base_url=OPENAI_API_BASE)

# Example call to the model
# The model can be any model which FuelIx supports. 
def get_openai_response(prompt, model="claude-3-5-sonnet", max_tokens=100):
    try:
        response = client.chat.completions.create(model=model,
        messages=[{"role": "user", "content": prompt}],
        max_tokens=max_tokens)
        return response.choices[0].message.content.strip()
    except Exception as e:
        print(f"Error: {e}")
        return None

# Example usage
if __name__ == "__main__":
    prompt = "Write a short poem about AI."
    result = get_openai_response(prompt)
    print(result)